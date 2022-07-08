package com.servustech.eduson.features.permissions.permissions;

import com.servustech.eduson.features.permissions.transactions.Transaction;
import com.servustech.eduson.features.permissions.ProductType;
import com.servustech.eduson.features.permissions.Benefit;
import com.servustech.eduson.features.files.File;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import java.time.ZonedDateTime;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "permissions")
public class Permission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transaction_id")
  private Transaction transaction;

	private Long userId;
	private Long ownerId;

	@Enumerated(EnumType.STRING)
	@Column(name = "product_type")
	private ProductType productType = ProductType.MODULE;
	private Long productId;
	private Long periodId;

	private Boolean active;
	private ZonedDateTime startTime;
	private ZonedDateTime expires;
	private ZonedDateTime endsAt;
	private ZonedDateTime addedDate;

	private String stripe;
	private Boolean cart;
	private Float value;

}
