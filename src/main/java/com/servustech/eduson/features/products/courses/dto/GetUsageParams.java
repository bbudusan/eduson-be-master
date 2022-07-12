package com.servustech.eduson.features.products.courses.dto;

import com.servustech.eduson.security.payload.StreamType;

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
public class GetUsageParams {
  private ZonedDateTime startDate;
  private ZonedDateTime endDate;
  private List<Long>userIds;
  private Long webinarId;
  private Long courseId;
  private StreamType streamType;
  private Long idAfter;
  private Integer count;
}
