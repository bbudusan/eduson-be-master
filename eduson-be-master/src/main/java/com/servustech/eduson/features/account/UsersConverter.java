package com.servustech.eduson.features.account;

import com.servustech.eduson.features.account.users.dto.UserDetailsResponseExt;
import com.servustech.eduson.features.account.lectors.LectorService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UsersConverter {

	private final LectorService lectorService;

	public UserDetailsResponseExt fromUserToUserDetailsResponse(User user) {
		if (user == null) {
			return new UserDetailsResponseExt();
		}
		var lector = lectorService.findByUserId(user.getId());
		return UserDetailsResponseExt.builder()
			.username(user.getUsername())
			.email(user.getEmail())
			.firstName(user.getFirstName())
			.lastName(user.getLastName())
			.fullName(user.getFullName())
			.id(user.getId())
			.accountStatus(user.getAccountStatus())
			.banned(user.isBanned())
			.active(user.isActive())
			.inactive(user.isInactive())
			.locked(user.isLocked())
			.profileImage(user.getProfileImage())
			.roles(user.getRoles())
			.lector(lector)
			.build();
	}
	public List<UserDetailsResponseExt> fromUserToUserDetailsResponseList(List<User> users) {
		return users
				.stream()
				.map(this::fromUserToUserDetailsResponse)
				.collect(Collectors.toList());
	}
}
