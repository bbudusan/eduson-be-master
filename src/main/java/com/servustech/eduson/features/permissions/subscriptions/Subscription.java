package com.servustech.eduson.features.permissions.subscriptions;

import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.Benefit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "subscriptions")
public class Subscription implements Benefit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// @Column(name = "name", unique = true)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private SubscriptionType type = SubscriptionType.PRIVATE;

	private String description;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "subscription")
	private List<SubscriptionProduct> subscriptionProducts;

	private String stripe;

	private Boolean published;
	public boolean getPublished() {
		return published == null || published;
	}

  public String getPriceStripe() {return null;}
  public void setPriceStripe(String stripe) {};

	// @Formula("(select * from subscription_periods sp where (sp.product_type =
	// 'SUBSCRIPTION' AND sp.product_id = id)")

	// @OneToMany(fetch = FetchType.LAZY)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "product_id")
	// @JoinColumn(name = "product_id")
	@Where(clause = "product_type = 'SUBSCRIPTION'")
	private List<SubscriptionPeriod> subscriptionPeriods;

	public Float getAmount(Long periodId) {
		var list = subscriptionPeriods.stream()
				.filter(subscriptionPeriod -> subscriptionPeriod.getPeriod().getId() == periodId)
				.collect(Collectors.toList());
		if (list.size() > 0) {
			return list.get(0).getPrice();
		} else {
			return null;
		}
	}
}
