package com.servustech.eduson.features.products.courses.dto;

import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.users.dto.UserDetailsResponseExt;
import com.servustech.eduson.features.categories.tags.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CourseResponseExt {
	private Long courseId;
	private String name;
	private float price;
	private ZonedDateTime addedDate;
	private String addedBy;
	private Long orderIndex;
	private Boolean favorited;
	private String description;
	private UserDetailsResponseExt lector;
	private File courseFile;
	private File imageFile;
	private java.sql.Time duration;
	private List<Tag> tags;
	private List<AdvertDto> adverts;
	private Boolean hasAccess;
}
