package com.servustech.eduson.features.permissions.permissions;

import com.servustech.eduson.features.permissions.ProductType;
import com.servustech.eduson.features.permissions.Benefit;
import com.servustech.eduson.features.files.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PermissionDto {

	private Long id;

	@NotBlank
	private Long userId;
	private Long ownerId;
	private String userEmail; // TODO not needed

	@NotBlank
	private ProductType productType;
	@NotBlank
	private Long productId;

	private Long periodId;

	private Boolean active;
	private ZonedDateTime startTime;
	private ZonedDateTime expires;
	private ZonedDateTime endsAt;
	private ZonedDateTime addedDate;

	private String stripe;

	// just for returning to the enduser:
	private String productName;
	private String periodName;
	private String userName;
	private String ownerName;

	private File invoice;

	private Float value;
	private Boolean cart;

	private Long quantity;
}
