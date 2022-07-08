package com.servustech.eduson.features.products.courses.dto;

import com.servustech.eduson.features.files.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.ZonedDateTime;
import java.sql.Time;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AdvertDto {
	private Long id;
  @Size(max = 500)
	private String name;
	// private List<Long> courseIds;
	private String description;
	private java.sql.Time duration;
  private Long fileId;
  private File file;
  @Size(max = 1000)
  private String onclick;

  private Time start;
  private Integer priority;
  private Long rule;
  private Boolean isInCourse;
}
