package com.servustech.eduson.features.permissions.subscriptions;

import com.servustech.eduson.features.permissions.ProductType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BenefitDto {
	
	// private Long id;
  // @NotBlank
  // @Size(min = 2, max = 500)
	private String name;
  @NotBlank
	private ProductType productType;
  @NotBlank
	private Long productId;
}
