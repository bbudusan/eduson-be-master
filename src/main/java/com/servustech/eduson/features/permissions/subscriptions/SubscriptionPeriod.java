package com.servustech.eduson.features.permissions.subscriptions;

import com.servustech.eduson.features.permissions.ProductType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "subscription_periods")
public class SubscriptionPeriod {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
  @JoinColumn(name = "period_id", nullable = false)
	private Period period;

  private Float price;
  private String stripe;

  @Enumerated(EnumType.STRING)
	@Column(name = "product_type")
  private ProductType productType = ProductType.SUBSCRIPTION;
  
  @Column(name = "product_id")
  private Long productId;

}
