package com.servustech.eduson.features.products.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.ZonedDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CourseDto {
  @NotBlank
  @Size(min = 2, max = 500)
	private String name;
  @NotBlank
	private float price;
	private List<Long> moduleIds;
  @NotBlank
  @Size(min = 2)
	private String description;
	private List<Long> tagIds;
  @NotBlank
	private long lectorId;
  @NotBlank
	private java.sql.Time duration;

  private Long imageFileId;
  private Long fileId;
  private ZonedDateTime publishedDate;
  private Boolean published;
}
