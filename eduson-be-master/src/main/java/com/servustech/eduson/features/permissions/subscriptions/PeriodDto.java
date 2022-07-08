package com.servustech.eduson.features.permissions.subscriptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PeriodDto {
	private Long id;
  private String name;
  private String description;
  private Interval interval;
  private Long intervalCount;

  private Float price;
}
