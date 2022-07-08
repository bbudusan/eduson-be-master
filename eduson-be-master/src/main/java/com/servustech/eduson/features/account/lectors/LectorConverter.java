package com.servustech.eduson.features.account.lectors;

import com.servustech.eduson.features.account.lectors.dto.LectorProfileResponseDto;
import com.servustech.eduson.features.account.lectors.dto.LectorViewResponse;
import com.servustech.eduson.features.account.lectors.dto.LectorDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.servustech.eduson.features.account.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class LectorConverter {

	public LectorViewResponse fromLectorToLectorViewResponse(Lector lector) {
		if (lector == null) {
			return new LectorViewResponse();
		}
		var user = lector.getUser();
		return LectorViewResponse.builder()
				.id(lector.getId())
				.userId(user.getId())
				.email(user.getEmail())
				.fullName(user.getFullName())
				.hasAccess(lector.isHasAccess())
				.published(lector.getPublished())
				.build();
	}

	public List<LectorViewResponse> fromLectorListToLectorViewResponseList(List<Lector> lectors) {
		return lectors
				.stream()
				.map(this::fromLectorToLectorViewResponse)
				.collect(Collectors.toList());
	}

	public LectorProfileResponseDto fromLectorToLectorProfile(Lector lector) {
		if (lector == null) {
			return new LectorProfileResponseDto();
		}
		User user = lector.getUser();

		return LectorProfileResponseDto.builder()
				.nameAndTitle(lector.getTitle().getTitle() + " " + user.getLastName() + " " + user.getFirstName())
				.description(lector.getDescription())
				.profileImage(user.getProfileImage())
				// .id(lector.getId())
				// .userId(user.getId())
				.id(user.getId())
				.build();

	}
	public LectorProfileResponseDto fromLectorToLectorNarrow(Lector lector) {
		User user = lector.getUser();

		return LectorProfileResponseDto.builder()
				.nameAndTitle(lector.getTitle().getTitle() + " " + user.getLastName() + " " + user.getFirstName())
				.id(user.getId())
				.build();

	}
	public LectorProfileResponseDto fromLectorToLectorWider(Lector lector) {
		User user = lector.getUser();

		return LectorProfileResponseDto.builder()
				.nameAndTitle(lector.getTitle().getTitle() + " " + user.getLastName() + " " + user.getFirstName())
				.profileImage(user.getProfileImage())
				.id(user.getId())
				.build();
	}

	public List<LectorProfileResponseDto> fromLectorToLectorProfileList(List<Lector> lectors) {
		return lectors
				.stream()
				.map(this::fromLectorToLectorProfile)
				.collect(Collectors.toList());
	}

	public LectorDto fromLectorToLectorDto(Lector lector) {
		User user = lector.getUser();

		return LectorDto.builder()
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.username(user.getUsername())
				.email(user.getEmail())
				.hasAccess(lector.isHasAccess())
				.description(lector.getDescription())
				.titleId(lector.getTitle().getId())
				.profileImage(user.getProfileImage())
				.build();

	}
}
