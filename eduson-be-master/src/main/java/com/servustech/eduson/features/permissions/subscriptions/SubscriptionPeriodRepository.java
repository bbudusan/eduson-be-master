package com.servustech.eduson.features.permissions.subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;

import com.servustech.eduson.features.permissions.ProductType;
import com.servustech.eduson.features.permissions.Benefit;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPeriodRepository extends JpaRepository<SubscriptionPeriod, Long> {

	// @Modifying
	// @Query("DELETE FROM SubscriptionPeriod SP WHERE SP.productId = :id AND SP.productType = :type")
	// void clearAll(@Param("id") Long id, @Param("type") ProductType type);
	@Query("SELECT SP from SubscriptionPeriod SP WHERE SP.productId = :productId AND SP.productType = :productType AND SP.period.id = :periodId")
	Optional<SubscriptionPeriod> findByData(@Param("productId") Long productId, @Param("productType") ProductType productType, @Param("periodId") Long periodId);
}
