package com.servustech.eduson.features.permissions;

import com.servustech.eduson.features.permissions.ProductType;
import com.servustech.eduson.features.permissions.permissions.PermissionDto;
import com.servustech.eduson.features.permissions.Benefit;
import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.permissions.permissions.PaymentType;

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
public class TransactionDto {

	private Long id;

	@NotBlank
	private Long userId;

	List<PermissionDto> permissions;

  private ZonedDateTime timestamp;
	private ZonedDateTime paidAt;

	private String transactionId;
	private String paymentIntent;

	private PaymentType paymentType;

	private Float value;

	// just for returning to the enduser:

	private String userName;

	private File invoice;
	private File transfer;
  private String data;

	private String invoiceId;
}
