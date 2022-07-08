package com.servustech.eduson.features.products.courses.dto;

import com.servustech.eduson.features.files.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CoursePublicViewResponse {

	private Long id;
	private String name;
	private float price;
	private ZonedDateTime addedDate;
	private String lector;
	private Boolean favorited;
	private File imageFile;
	private java.sql.Time duration;
}
