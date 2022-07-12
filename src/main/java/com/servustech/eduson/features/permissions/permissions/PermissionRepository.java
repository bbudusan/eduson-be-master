package com.servustech.eduson.features.permissions.permissions;

import com.servustech.eduson.features.permissions.ProductType;

import com.servustech.eduson.features.permissions.transactions.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
	List<Permission> findAllByUserId(Long userId);
	@Query("select p from Permission p where p.stripe = :stripe")
	List<Permission> findByStripe(@Param("stripe") String stripe, Pageable pageable);

	@Query("Select p FROM Permission p WHERE " +
			"(:userIdLength = 0 OR p.userId IN :userIds) " +
			// "AND (:paymentType IS NULL OR p.paymentType = :paymentType) " +
			"AND (:productType IS NULL OR p.productType = :productType) " +
			"AND (:productIdLength = 0 OR p.productId IN :productIds) " +
			"AND (:periodId IS NULL OR p.periodId = :periodId OR (:periodId = 0 AND p.periodId IS NULL))")
	Page<Permission> findAllByData(
			@Param("userIds") List<Long> userIds,
			@Param("userIdLength") int userIdLength,
			@Param("productType") ProductType productType,
			@Param("productIds") List<Long> productIds,
			@Param("productIdLength") int productIdLength,
			@Param("periodId") Long periodId,
			Pageable pageable);

	List<Permission> deleteAllByOwnerIdAndCart(Long ownerId, boolean cart);
	List<Permission> findAllByOwnerIdAndCart(Long ownerId, boolean cart);
	List<Permission> findAllByOwnerIdAndTransaction(Long ownerId, Transaction transaction);

}
