package com.servustech.eduson.features.account;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.account.role.RoleName;
import com.servustech.eduson.features.account.role.RoleService;
import com.servustech.eduson.features.account.lectors.LectorService;
import com.servustech.eduson.features.account.lectors.Lector;
import com.servustech.eduson.features.account.users.dto.UserDto;
import com.servustech.eduson.features.account.UsersConverter;
import com.servustech.eduson.features.account.users.dto.UserDetailsResponseExt;
import com.servustech.eduson.features.files.FileService;
import com.servustech.eduson.security.auth.AuthService;
import com.servustech.eduson.features.products.courses.CourseRepository;
import com.servustech.eduson.features.products.webinars.WebinarCoordinatorRepository;
import com.servustech.eduson.features.products.liveEvents.LiveEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
@Service
@AllArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	private final WebinarCoordinatorRepository webinarCoordinatorRepository;
	private final LiveEventRepository liveEventRepository;
	private final RoleService roleService;
	private final LectorService lectorService;
	private final AuthService authService;
	private final FileService fileService;
	private final UsersConverter usersConverter;
  private static final String RO = "ro/";
	private static final String COUNTRIES = "countries/";

	public List<UserViewResponse> getAllByRole(RoleName name) {
		var role = roleService.findByRoleName(name);

		List<User> users = userRepository.findAllByRoles(role);
		List<UserViewResponse> responseList = new ArrayList<>();

		users.forEach(user -> {
			var response = new UserViewResponse();

			response.setUserId(user.getId());
			response.setName(user.getFullName());
			response.setEmail(user.getEmail());
			// response.setHasAccess();

			responseList.add(response);
		});

		return responseList;
	}

	// public List<UserViewResponse> getAll() {
	// List<User> users = userRepository.findAll();
	// List<UserViewResponse> responseList = new ArrayList<>();

	// users.forEach(user -> {
	// var response = new UserViewResponse();

	// response.setUserId(user.getId());
	// response.setName(user.getFullName());
	// response.setEmail(user.getEmail());

	// responseList.add(response);
	// });

	// return responseList;
	// }

	public User findById(Long id) {

		return userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("user-w-id-not-exist"));

	}

	public User findByLectorId(Long id) {
		return lectorService.findById(id).getUser();
	}

	public UserDetailsResponseExt findById2(Long userId) {
		var user = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("user-w-id-not-exist")); // TODO
		return usersConverter.fromUserToUserDetailsResponse(user);
	}

	public List<User> findAllByIds(List<Long> ids) {
		return userRepository.findAllById(ids);
	}

	@Transactional
	public UserDetailsResponseExt updateUser(UserDto userDto, MultipartFile profilePicture) {
		var user = findById(userDto.getId());
		if (!user.getUsername().equals(userDto.getUsername())) {
			authService.verifyIfUsernameOrEmailExists(userDto.getUsername(), null);
		}
		if (!user.getEmail().equals(userDto.getEmail())) {
			authService.verifyIfUsernameOrEmailExists(null, userDto.getEmail());
		}
		user.setUsername(userDto.getUsername());
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setEmail(userDto.getEmail());
		if (userDto.getPassword() != null) {
			if (!PasswordValidator.isValid(userDto.getPassword())) {
				throw new CustomException("password-too-easy");
			}
			user.setPassword(userDto.getPassword());
		}
		if (profilePicture != null) {
			var image = fileService.saveWithFile(profilePicture);
			user.setProfileImage(image);
		}
		if (userDto.isSendWelcomeEmail()) {
			authService.sendWelcomeEmail(user);
		}

		return usersConverter.fromUserToUserDetailsResponse(user);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("user-by-email-not-found"));
	}
	public User findByStripe(String stripe) {
		return userRepository.findByStripe(stripe).orElseThrow(() -> new NotFoundException("user-by-stripe-not-found"));
		// TODO this is a serious problem which may need a refund!
	}

	public void deleteUser(Long userId) {
		var user = findById(userId);
		userRepository.delete(user);
	}

	public boolean isLectorLinkedToAnyProduct(Long lectorId) {
		var lector = lectorService.findById(lectorId);
		var user = lector.getUser();
		return isLinkedToAnyProduct(user);
	}

	public boolean isLinkedToAnyProduct(Long userId) {
		var user = findById(userId);
		return isLinkedToAnyProduct(user);
	}
	public boolean isLinkedToAnyProduct(User user) {
		Long courseCount = courseRepository.courseCountOfLector(user.getId());
		if (courseCount > 0)
			return true;
		Long webinarCount = webinarCoordinatorRepository.webinarCountOfCoordinator(user.getId());
		if (webinarCount > 0)
			return true;
		Long eventCount = liveEventRepository.countByCoordinators(user);
		if (eventCount > 0)
			return true;
		eventCount = liveEventRepository.countByLectors(user);
		if (eventCount > 0)
			return true;
		return false;
	}

	public Page<UserViewResponse> viewUsers(String filterByName, Pageable pageable, RoleName roleName) {

		var role = roleService.findByRoleName(roleName);

		var sort = pageable.getSort();
		var sortCount = 0;
		for (Sort.Order order : sort) {
			sortCount++;
		}
		if (sortCount == 0) {
			Sort sort2 = JpaSort.unsafe(Sort.Direction.ASC, "firstName");
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort2);
		}

		if (filterByName == null) {
			filterByName = ""; // TODO apply this elsewhere
		}
		var userPage = userRepository.findAllByRolesAndDataContains(role, filterByName, pageable);
		// List<User> users = userRepository.findAllByRoles(role);
		// var userPage =
		// userRepository.findAllAndFilterByNameAndFilterByRole(filterByName,
		// role.getId(), pageable);

		List<UserViewResponse> responseList = new ArrayList<>();
		var users = userPage.getContent();
		users.forEach(user -> {
			var response = new UserViewResponse();

			response.setUserId(user.getId());
			response.setName(user.getFullName());
			response.setEmail(user.getEmail());
			response.setIsAdmin(user.isAdmin());
			response.setIsLector(user.isLector());

			// if (user.getFullName().contains(filterByName)) responseList.add(response);
			responseList.add(response);
		});
		return new PageImpl<>(responseList, pageable,
				userPage.getTotalElements());
	}

	@Transactional
	public void toggleRole(Long userId, RoleName role, User admin) {
		var user = findById(userId);
		if (role == RoleName.ROLE_ADMIN) {
			if (user.equals(admin)) {
				throw new CustomException("admin-rights-themself");
			}
			if (user.isAdmin()) {
				user.getRoles().remove(roleService.getAdminRole());
			} else {
				user.getRoles().add(roleService.getAdminRole());
			}
		}
		if (role == RoleName.ROLE_LECTOR) {
			if (user.isLector()) {
				user.getRoles().remove(roleService.getLectorRole());
			} else {
				user.getRoles().add(roleService.getLectorRole());
				if (lectorService.findByUserId(userId) == null) {
					lectorService.createDefault(user);
				}
			}
		}
	}

	public List<UserShortResponse> viewUsersShort(String filterByName, RoleName roleName) {

		var role = roleService.findByRoleName(roleName);

		if (filterByName == null) {
			filterByName = "";
		}
		var userPage = userRepository.findAllByRolesAndDataContains(role, filterByName, null);

		List<UserShortResponse> responseList = new ArrayList<>();
		var users = userPage.getContent();
		users.forEach(user -> {
			var response = new UserShortResponse();

			response.setId(user.getId());
			response.setName(user.getFullName());
			responseList.add(response);
		});

		return responseList;
	}

	private String getCityResource(String country, String id) {
		// TODO sanitize country, county and id, as maybe the user can ask anything, e.g. country = "../../.ssh/keys"
		if (id == null) {
			id = "_judete";
		}
		String text = null;
		try {
			InputStream inputStream = new ClassPathResource("classpath:" + COUNTRIES + country + id + ".json").getInputStream();
			text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			// development case:
			try {
				File file = ResourceUtils.getFile("classpath:" + COUNTRIES + country + id + ".json");
				text = new String(new FileInputStream(file).readAllBytes(), StandardCharsets.UTF_8);
			} catch (Exception ee) {
				return "[]";
			}
		}
		return text;
	}
	public String getCountries() {
		return getCityResource("countries", "");
	}
	public String getCountiesRo() {
		return getCityResource(RO, null);
	}
	public String getCitiesRo(String county) {
		if (county == null || county.length() != 2) { // TODO is this enough for security?
			return "[]";
		}
		return getCityResource(RO, county);
	}
}
