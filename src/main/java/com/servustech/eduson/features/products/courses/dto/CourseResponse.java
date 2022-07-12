package com.servustech.eduson.features.products.courses.dto;

import com.servustech.eduson.features.files.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CourseResponse {

	// TO DO : add duration field + add it in Course entity

	private Long id;
	private String name;
	private java.sql.Time duration;
	private boolean isInWebinar;
	private float price;
	private ZonedDateTime addedDate;
	private String addedBy;
	private String lector;
	private Long orderIndex;
	// private File imageFile;
}
