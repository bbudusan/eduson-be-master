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
public class CourseViewResponse {

	private Long id;
	private String name;
	private float price;
	private ZonedDateTime addedDate;
	private String addedBy;
	private String lector;
	private Boolean favorited;
	private File imageFile;
	private java.sql.Time duration;
	private ZonedDateTime publishedDate;
	private Boolean published;
}
