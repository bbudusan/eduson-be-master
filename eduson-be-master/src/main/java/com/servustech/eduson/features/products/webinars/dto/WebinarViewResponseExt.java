package com.servustech.eduson.features.products.webinars.dto;

import com.servustech.eduson.features.files.File;
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
public class WebinarViewResponseExt {

	private Long id;
	private String name;
	private String acronym;
	private Integer credits;
	private String addedBy;
	private ZonedDateTime addedDate;
	private ZonedDateTime startTime;
	private ZonedDateTime endTime;
	private List<UserDetailsResponseExt> coordinators;
	private List<Tag> tags;
	private String description;
	private List<Module> modules;
	private float price;
	private Boolean favorited;
	private File imageFile;
	private Boolean hasAccess;
	private Boolean published;
}
