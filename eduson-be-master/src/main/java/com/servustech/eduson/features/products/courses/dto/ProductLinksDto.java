package com.servustech.eduson.features.products.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductLinksDto {
  Boolean courses;
  Boolean webinars;
  Boolean liveEvents;
  Boolean modules;
  Boolean subscribers;
  Boolean subscriptions;
}
