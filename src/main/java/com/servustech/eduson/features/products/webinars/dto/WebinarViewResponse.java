package com.servustech.eduson.features.products.webinars.dto;

import com.servustech.eduson.features.files.File;

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
public class WebinarViewResponse {

	private Long id;
	private String name;
	private String acronym;
	private Integer credits;
	private String addedBy;
	private ZonedDateTime addedDate;
	private ZonedDateTime startTime;
	private ZonedDateTime endTime;
	private List<String> coordinators;
	private Boolean favorited;
	private File imageFile;
	private float price;
	private Boolean published;
}
