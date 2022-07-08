package com.servustech.eduson.features.products.liveEvents;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.UserService;
import com.servustech.eduson.features.files.FileService;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.products.liveEvents.dto.LiveEventDto;
import com.servustech.eduson.features.products.liveEvents.dto.LiveEventViewResponse;
import com.servustech.eduson.features.products.liveEvents.dto.SignatureResponse;
import com.servustech.eduson.features.categories.modules.ModuleService;
import com.servustech.eduson.features.categories.tags.TagService;
import com.servustech.eduson.features.products.courses.dto.ProductLinksDto;
import com.servustech.eduson.exceptions.CustomException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.security.InvalidKeyException;
import javax.xml.bind.DatatypeConverter;

@Service
@AllArgsConstructor
public class LiveEventService {

	private final LiveEventRepository liveEventRepository;
	private final LiveEventsConverter liveEventsConverter;
	private final FavoriteLiveEventRepository favoriteLiveEventRepository;
	private final UserService userService;
	private final FileService fileService;
	private final ModuleService moduleService;
	private final TagService tagService;
	private final PermissionsService permissionsService;

	public LiveEvent create(LiveEventDto liveEventDto, User user, MultipartFile imageFile) {
		var coordinators = userService.findAllByIds(liveEventDto.getCoordinatorIds());
		var lectors = userService.findAllByIds(liveEventDto.getLectorIds());

		var image = imageFile == null ? fileService.findById(-1L) : fileService.saveWithFile(imageFile);

		var liveEvent = LiveEvent.builder()
				.name(liveEventDto.getName())
				.price(liveEventDto.getPrice())
				.description(liveEventDto.getDescription())
				.credits(liveEventDto.getCredits())
				.admin(user)
				.coordinators(coordinators)
				.lectors(lectors)
				.imageFile(image)
				.startTime(liveEventDto.getStartTime())
				.endTime(liveEventDto.getEndTime())
				.addedDate(ZonedDateTime.now())
				.modules(moduleService.findAllByIds(liveEventDto.getModuleIds()))
				.tags(tagService.findAllByIds(liveEventDto.getTagIds()))
				.credits(liveEventDto.getCredits())
				.startTime(liveEventDto.getStartTime())
				.endTime(liveEventDto.getEndTime())
				.session(liveEventDto.getSession())
				.published(liveEventDto.getPublished())
				.build();
		return liveEventRepository.save(liveEvent);
	}

	public void deleteLiveEvent(Long liveEventId) {
		var liveEvent = findById(liveEventId);
		liveEventRepository.delete(liveEvent);
	}

	public LiveEvent findById(Long liveEventId) {
		return liveEventRepository.findById(liveEventId)
				.orElseThrow(() -> new NotFoundException("live-event-w-id-not-exist"));
	}

	public LiveEventViewResponse findById2(Long liveEventId, User user) {
		var liveEvent = liveEventRepository.findById(liveEventId)
				.orElseThrow(() -> new NotFoundException("live-event-w-id-not-exist")); // TODO
		if (!user.isAdmin() && !liveEvent.getPublished()) {
			throw new NotFoundException("live-event-w-id-not-exist");
		}
		return liveEventsConverter.fromEventToEventViewResponse(liveEvent, user);

	}

	public Page<LiveEventViewResponse> getAllLiveEvents(Pageable pageable, String filterByName, User user) {
		var liveEventPage = user.isAdmin() ? 
			liveEventRepository.findAllAndFilterByName(filterByName, pageable) :
			liveEventRepository.findAllByPublishedAndFilterByName(filterByName, pageable);
		var liveEvents = liveEventsConverter.fromEventListToEventViewResponseList(liveEventPage.getContent(), user);
		return new PageImpl<>(liveEvents, pageable, liveEventPage.getTotalElements());
	}

	public List<LiveEventViewResponse> getNewLiveEvents(User user) {
		Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "addedDate");
		Pageable pageable = PageRequest.of(0, 6, sort2);
		var liveEventList = liveEventRepository.findAllByPublished(pageable);
		return liveEventsConverter.fromEventListToEventViewResponseList(liveEventList.getContent(), user);
	}

	public Page<LiveEventViewResponse> searchLiveEvents(List<Long> tagIdsp, List<Long> tagIdsn, String filterByName, Pageable pageable, User user) {
		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "addedDate");
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}
		var liveEventPage = liveEventRepository.searchBy(tagIdsp, tagIdsn, filterByName, pageable);
		return new PageImpl<>(
				liveEventsConverter.fromEventListToEventViewResponseList(liveEventPage.getContent(), user),
				pageable, liveEventPage.getTotalElements());
	}

	public Page<LiveEventViewResponse> viewLiveEvents(String filterByName, Pageable pageable, User user) {
		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.DESC, "addedDate");
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}
		var eventsPage = liveEventRepository.findAllAndFilterByName(filterByName, pageable);
		var events = liveEventsConverter.fromEventListToEventViewResponseList(eventsPage.getContent(), user);
		return new PageImpl<>(events, pageable, eventsPage.getTotalElements());
	}

	@Transactional
	public LiveEventViewResponse updateLiveEvent(Long liveEventId, LiveEventDto request, MultipartFile imageFile) {

		var liveEvent = findById(liveEventId);

		liveEvent.setName(request.getName());
		liveEvent.setPrice(request.getPrice());
		liveEvent.setDescription(request.getDescription());
		liveEvent.setStartTime(request.getStartTime());
		liveEvent.setEndTime(request.getEndTime());
		liveEvent.setCoordinators(userService.findAllByIds(request.getCoordinatorIds()));
		liveEvent.setLectors(userService.findAllByIds(request.getLectorIds()));
		liveEvent.setCredits(request.getCredits());
		liveEvent.setModules(moduleService.findAllByIds(request.getModuleIds()));
		liveEvent.setCredits(request.getCredits());
		liveEvent.setSession(request.getSession());
		liveEvent.setTags(tagService.findAllByIds(request.getTagIds()));
		liveEvent.setPublished(request.getPublished());

		if (imageFile != null) {
			var imageFile2 = fileService.saveWithFile(imageFile);
			liveEvent.setImageFile(imageFile2);
		} else if (request.getImageFileId() != null) {
			liveEvent.setImageFile(fileService.findById(request.getImageFileId()));
		} else {
			liveEvent.setImageFile(fileService.findById(-1L));
		}

		return liveEventsConverter.fromEventToEventViewResponse(liveEvent, null);

	}

	public static SignatureResponse generateSignature(Long meetingNumber, Integer role) {

		String apiKey = "AauqiBBJSnqg1ngOZfN7DQ"; String apiSecret = "VwT471fJGtlgSOncL4WQTeddZvIjCrrxiSKo";
		try {
			Mac hasher = Mac.getInstance("HmacSHA256");
			String ts = Long.toString(System.currentTimeMillis() - 30000);
			String msg = String.format("%s%d%s%d", apiKey, meetingNumber, ts, role);

			hasher.init(new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256"));

			String message = Base64.getEncoder().encodeToString(msg.getBytes());
			byte[] hash = hasher.doFinal(message.getBytes());

			String hashBase64Str = DatatypeConverter.printBase64Binary(hash);
			String tmpString = String.format("%s.%d.%s.%d.%s", apiKey, meetingNumber, ts, role, hashBase64Str);
			String encodedString = Base64.getEncoder().encodeToString(tmpString.getBytes());

			return SignatureResponse.builder().signature(encodedString.replaceAll("\\=+$", "")).build();

		} catch (NoSuchAlgorithmException e) {
		} catch (InvalidKeyException e) {
		}
		throw new CustomException("could-not-generate-signature");
	}

	public Page<LiveEventViewResponse> getFavoritedLiveEvents(Pageable pageable, String filterByName, User user) {
		if (filterByName == null) {
			filterByName = "";
		}
		var liveEventPage = favoriteLiveEventRepository.findAllByUserAndLiveEventNameContainsOrderByLiveEventAddedDateDesc(user, filterByName, pageable);
		var liveEvents = liveEventsConverter.fromEventListToEventViewResponseList(
			liveEventPage.getContent().stream().map(fle -> fle.getLiveEvent()).collect(Collectors.toList()), user);
		return new PageImpl<>(liveEvents, pageable, liveEventPage.getTotalElements());
	}

	public Page<LiveEventViewResponse> getMyLiveEvents(Pageable pageable, String filterByName, User user) {
		// pageableeee? // it seems that we have to treat the library like the favorites
		// in the database. those should be "views".
		// this time we use the current methods.
		// TODO we should invent something in order not to do the same query twice
		if (filterByName == null) {
			filterByName = "";
		}
		List<LiveEvent4Check> allLiveEventsChecked = permissionsService.getLiveEvents(user, filterByName);
		Page<LiveEvent> liveEventPage = liveEventRepository
				.findByIdIn(allLiveEventsChecked.stream().map(le -> le.getId()).collect(Collectors.toList()), pageable);

		var liveEvents = liveEventsConverter.fromEventListToEventViewResponseList(
			liveEventPage.getContent(), user);
		return new PageImpl<>(liveEvents, pageable, liveEventPage.getTotalElements());
	}
	public Page<LiveEventViewResponse> getMyUpcomingLiveEvents(Pageable pageable, String filterByName, User user) { // TODO we might use a narrower LiveEventViewResponse
		// almost the same as the previous
		List<LiveEvent4Check> allLiveEventsChecked = permissionsService.getLiveEvents(user, filterByName);
		Page<LiveEvent> liveEventPage = liveEventRepository
				.findByIdInAndEndTimeGreaterThanOrderByStartTimeAsc(allLiveEventsChecked.stream().map(le -> le.getId()).collect(Collectors.toList()), ZonedDateTime.now(), pageable);
		var liveEvents = liveEventsConverter.fromEventListToEventViewResponseList(
			liveEventPage.getContent(), user);
		return new PageImpl<>(liveEvents, pageable, liveEventPage.getTotalElements());
	}

	public ProductLinksDto hasLinks(Long id) {
    Pageable pageable = PageRequest.of(0, 1, JpaSort.unsorted());
		return ProductLinksDto.builder()
			.modules(liveEventRepository.findFirstModule(id, pageable).size() > 0)
			.subscribers(liveEventRepository.findFirstPermission(id, pageable).size() > 0)
			.subscriptions(liveEventRepository.findFirstSubscription(id, pageable).size() > 0)
		.build();
	}

	@Transactional
	public void togglePublish(Long liveEventId) {
		var liveEvent  = findById(liveEventId);
		var published  = liveEvent.getPublished();
		liveEvent.setPublished(!published);
	}
	@Transactional
	public void publish(Long liveEventId) {
		var liveEvent  = findById(liveEventId);
		liveEvent.setPublished(true);
	}
	@Transactional
	public void unpublish(Long liveEventId) {
		var liveEvent  = findById(liveEventId);
		liveEvent.setPublished(false);
	}
}
