package com.servustech.eduson.features.search;

import com.servustech.eduson.features.products.webinars.dto.WebinarPublicViewResponse;

import org.springframework.data.domain.Page;

import com.servustech.eduson.features.account.lectors.dto.LectorProfileResponseDto;
import com.servustech.eduson.features.products.courses.dto.CoursePublicViewResponse;
import com.servustech.eduson.features.products.liveEvents.dto.LiveEventViewResponse;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SearchResult {
  Page<CoursePublicViewResponse> courses;
  Page<WebinarPublicViewResponse> webinars;
  Page<LiveEventViewResponse> events;
  Page<LectorProfileResponseDto> lectors;
}
