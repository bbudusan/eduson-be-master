package com.servustech.eduson.features.permissions.transactions;

import com.servustech.eduson.features.permissions.permissions.PaymentType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  // Optional<Transaction> findByPermissionId(Long permissionId);
	// Page<Transaction> findAllByPermissionId(Long permissionId, Pageable pageable);
  Optional<Transaction> findByTransactionId(String stripe);

 	// @Query("SELECT p FROM Transaction p WHERE (p.paymentType = 'TRANSFER')")
	// List<Transaction> getLastTransferIntent(Pageable pageable);
 	@Query("SELECT p FROM Transaction p WHERE (invoiceId IS NOT NULL)")
	List<Transaction> getLastInvoiceId(Pageable pageable);

  Optional<Transaction> findByPaymentIntent(String stripe);

	@Query("Select p FROM Transaction p WHERE (p.paidAt IS NOT NULL OR p.paymentType = 'TRANSFER') " +
	"AND (:userIdLength = 0 OR p.userId IN :userIds) " +
	"AND (:paymentType IS NULL OR p.paymentType = :paymentType)")
Page<Transaction> findAllByData(
	@Param("userIds") List<Long> userIds,
	@Param("userIdLength") int userIdLength,
	@Param("paymentType") PaymentType paymentType,
	Pageable pageable);


}
