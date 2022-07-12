package com.servustech.eduson.features.products.liveEvents;

import com.servustech.eduson.features.products.liveEvents.dto.LiveEventViewResponse;
import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.UsersConverter;
import com.servustech.eduson.features.permissions.PermissionsService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class LiveEventsConverter {

	private final UsersConverter usersConverter;
	private final PermissionsService permissionsService;
	private final LiveEvent4CheckRepository liveEvent4CheckRepository;

	
	public LiveEventViewResponse fromEventToEventViewResponse(LiveEvent event, User user) {
		if (event == null) {
			return new LiveEventViewResponse();
		}
		var userHasAccess = permissionsService.hasAccessTo(user,
		liveEvent4CheckRepository.findById(event.getId()).orElseThrow(() -> new NotFoundException("live-event-w-id-not-exist")));

		return LiveEventViewResponse.builder()
									.id(event.getId())
									.name(event.getName())
									.credits(event.getCredits())
									.addedDate(event.getAddedDate())
									.addedBy(event.getAdmin().getFullName())
									.startTime(event.getStartTime())
									.endTime(event.getEndTime())
									.coordinators(usersConverter.fromUserToUserDetailsResponseList(event.getCoordinators()))
									.lectors(usersConverter.fromUserToUserDetailsResponseList(event.getLectors()))
									.modules(event.getModules())
									.session(userHasAccess ? event.getSession() : "")
									.tags(event.getTags())
									.favorited(event.isFavorited(user))
									.description(event.getDescription())
									.price(event.getPrice())
									.imageFile(event.getImageFile())
									.published(event.getPublished())
									.hasAccess(userHasAccess)
									.build();
							
	}
	
	public List<LiveEventViewResponse> fromEventListToEventViewResponseList(List<LiveEvent> events, User user) {
		return events
				.stream()
				.map(event -> fromEventToEventViewResponse(event, user))
				.collect(Collectors.toList());
	}
}
