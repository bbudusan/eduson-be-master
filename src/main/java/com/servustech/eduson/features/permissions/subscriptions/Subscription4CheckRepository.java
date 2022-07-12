package com.servustech.eduson.features.permissions.subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Subscription4CheckRepository extends JpaRepository<Subscription4Check, Long> {
	// List<Subscription4Check> findAllByType(SubscriptionType subscriptionType);
}
