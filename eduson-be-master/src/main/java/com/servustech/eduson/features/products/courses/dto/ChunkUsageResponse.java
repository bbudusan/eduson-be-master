package com.servustech.eduson.features.products.courses.dto;

import com.servustech.eduson.security.payload.StreamType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;
import java.time.LocalTime;
import java.util.List;
import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChunkUsageResponse {
	private Long id;

  private ZonedDateTime point;
	private Long actionId;

  private Long userId;
  private String username;

	private Long chunkId;
  private Long webinarId;
  private Long courseId;
  private Long advertId;
	private Long number;
  private StreamType streamType;
	private String quality;
	private LocalTime duration;
}
