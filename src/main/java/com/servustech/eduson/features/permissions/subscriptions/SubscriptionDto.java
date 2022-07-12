package com.servustech.eduson.features.permissions.subscriptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.servustech.eduson.features.permissions.subscriptions.BenefitDto;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubscriptionDto {
	
	private Long id;
  @NotBlank
  @Size(min = 2, max = 40)
	private String name;
  @NotBlank
	private SubscriptionType type;
	private List<PeriodDto> periods;
  @NotBlank	
 	private String description;
  private List<BenefitDto> benefits;

  private Boolean hasAccess;
  private Boolean published;
}
