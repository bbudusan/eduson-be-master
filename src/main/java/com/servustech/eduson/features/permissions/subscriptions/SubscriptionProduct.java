package com.servustech.eduson.features.permissions.subscriptions;

import com.servustech.eduson.features.permissions.ProductType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "subscription_products")
public class SubscriptionProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_id")
	private Subscription subscription;

	@Enumerated(EnumType.STRING)
	@Column(name = "product_type")
	private ProductType productType = ProductType.MODULE;
	private Long productId;
}
