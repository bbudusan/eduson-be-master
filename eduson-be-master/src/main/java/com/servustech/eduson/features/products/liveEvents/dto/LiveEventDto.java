package com.servustech.eduson.features.products.liveEvents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LiveEventDto {
	@NotBlank
  @Size(min = 2, max = 500)
  private String name;
  @NotBlank
  private float price;
  private List<Long> moduleIds;
  @NotBlank
  private Integer credits;
  @NotBlank
  @Size(min = 2)
  private String description;
  private List<Long> tagIds;
  @NotBlank
  private ZonedDateTime startTime;
  @NotBlank
  private ZonedDateTime endTime;
  @NotBlank
  private List<Long> coordinatorIds;
  @NotBlank
  private List<Long> lectorIds;
	private String session;

  private Long imageFileId;

  private Boolean published;
}
