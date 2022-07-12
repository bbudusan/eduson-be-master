package com.servustech.eduson.features.permissions.subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
	List<Subscription> findAllByType(SubscriptionType subscriptionType);
}
