package com.servustech.eduson.features.products.courses.dto;

import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.users.dto.UserDetailsResponseExt;
import com.servustech.eduson.features.categories.tags.Tag;
import com.servustech.eduson.features.categories.modules.Module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CourseViewResponseExt {
	private Long id;
	private String name;
	private float price;
	private ZonedDateTime addedDate;
	private String addedBy;
	private UserDetailsResponseExt lector;
	private Boolean favorited;
	private File courseFile;
	// private File courseFile; // Thumbnail TODO
	private List<Tag> tags;
	private String description;
	private List<Module> modules;
	private File imageFile;
	private java.sql.Time duration;
	private ZonedDateTime publishedDate;
	private Boolean published;
	private List<AdvertDto> adverts;
	private Boolean hasAccess;
}
