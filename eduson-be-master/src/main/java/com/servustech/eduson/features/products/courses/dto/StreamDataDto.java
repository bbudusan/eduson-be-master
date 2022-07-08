package com.servustech.eduson.features.products.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StreamDataDto {
  Long index;
  Long id;
  String title;
  Long advertId;
  String advertTitle;
  String advertDescription;
  String advertOnClick;
  Long webinarId;
  // Lector etc.
}
