package com.servustech.eduson.features.permissions.subscriptions;

import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.Benefit;
import com.servustech.eduson.features.permissions.Benefit4Check;
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
public class Subscription4Check implements Benefit4Check {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "subscription")
	private List<SubscriptionProduct> subscriptionProducts;

	public boolean isOrHas(Benefit4Check benefit, PermissionsService ps) {
		if (benefit instanceof Subscription4Check && benefit.getId().equals(id)) {
			return true;
		}
		return this.subscriptionProducts.stream()
				.anyMatch(product -> ps.getProduct(product.getProductType(), product.getProductId()).isOrHas(benefit, ps));
	}
	public boolean isOrHasAll(List<Benefit4Check> benefits, PermissionsService ps) {
		for(Benefit4Check b : benefits) if(!isOrHas(b, ps)) return false;
		return true;
	}

}
