package com.servustech.eduson.features.permissions.transactions;

import com.servustech.eduson.features.permissions.TransactionDto;
import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.features.permissions.permissions.Permission;
import com.servustech.eduson.features.permissions.permissions.PaymentType;
import com.servustech.eduson.features.account.UserService;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;

import javax.transaction.Transactional;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;
import java.time.ZoneId;
@Service
@AllArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final UserService userService;

  @Transactional
  public TransactionDto createTransaction(TransactionDto transactionDto) {
    var transaction = Transaction.builder()
      // .permissions(permissions)
      .userId(transactionDto.getUserId())
      .timestamp(transactionDto.getTimestamp())
      .paidAt(transactionDto.getPaidAt())
      .value(transactionDto.getValue())
      .transactionId(transactionDto.getTransactionId())
      .invoiceId(transactionDto.getInvoiceId())
      .paymentType(transactionDto.getPaymentType())
      // .invoice(transactionDto.getInvoiceId() != null ? transactionDto.getInvoiceFile() : transactionDto.getTransferFile())
    .build();
    // TODO
    transaction = transactionRepository.save(transaction);
    return getTransaction(transaction);
  }
  @Transactional
  public TransactionDto updateTransaction(TransactionDto transactionDto) {
    var transaction = transactionRepository.findById(transactionDto.getId()).orElseThrow(() -> new NotFoundException("transaction-not-found"));
    // .permissions(permissions)
    transaction.setUserId(transactionDto.getUserId());
    transaction.setTimestamp(transactionDto.getTimestamp());
    transaction.setPaidAt(transactionDto.getPaidAt());
    transaction.setValue(transactionDto.getValue());
    transaction.setTransactionId(transactionDto.getTransactionId());
    transaction.setInvoiceId(transactionDto.getInvoiceId());
    transaction.setPaymentType(transactionDto.getPaymentType());
    // transaction.invoice(transactionDto.getInvoiceId() != null ? transactionDto.getInvoiceFile() : transactionDto.getTransferFile())
    // TODO
    transaction = transactionRepository.save(transaction);
    return getTransaction(transaction);
  }

  public TransactionDto getTransaction(Long id) {
    var transaction = transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("transaction-not-found"));
    return getTransaction(transaction);
  }
  public TransactionDto getTransaction(Transaction transaction) {
    return TransactionDto.builder()
      // TODO filterByName
      .id(transaction.getId())
      .userId(transaction.getUserId())
      .userName(userService.findById(transaction.getUserId()).getFullName())
      .timestamp(transaction.getTimestamp())
      .paidAt(transaction.getPaidAt())
      .value(transaction.getValue())
      .transactionId(transaction.getTransactionId())
      .invoiceId(transaction.getInvoiceId())
      .paymentType(transaction.getPaymentType())
      .invoice(transaction.getInvoiceId() != null ? transaction.getInvoiceFile() : transaction.getTransferFile())
    .build();
  }

  // public Transaction getTransactionByPermissionId(Long permissionId) {
  //   Sort sort = JpaSort.unsafe(Sort.Direction.DESC, "timestamp");
  //   Pageable pageable = PageRequest.of(0, 1, sort);
  //   var transactions = transactionRepository.findAllByPermissionId(permissionId, pageable);
  //   var transactionList = transactions.getContent();
  //   if (transactionList.size() > 0) {
  //     return transactionList.get(0); // get last
  //   } else {
  //     throw new NotFoundException("not-found");
  //   }
  // }

  // public Page<TransactionDto> getAllTransactions(Permission permission, Pageable pageable) {
  //   var transactions = permission == null ? transactionRepository.findAll(pageable)
  //       : transactionRepository.findAllByPermissionId(permission.getId(), pageable);
  //   var list = transactions.getContent().stream()
  //       .map(transaction -> TransactionDto.builder().id(transaction.getId())
  //           .permissionId(transaction.getPermission().getId()).transactionId(transaction.getTransactionId())
  //           .timestamp(transaction.getTimestamp()).value(transaction.getValue()).build())
  //       .collect(Collectors.toList());
  //   return new PageImpl<>(list, pageable, transactions.getTotalElements());

  // }

  public String deleteTransaction(Long permissionId) {
    return "TODO"; // TODO do we need this?
  }

  // public String getNextTransferIntent() {
  //   Sort sort = JpaSort.unsafe(Sort.Direction.DESC, "startTime");
  //   Pageable pageable = PageRequest.of(0, 1, sort);
  //   String last = "0";
  //   List<Transaction> list = transactionRepository.getLastTransferIntent(pageable);
  //   if (list.size() > 0) {
  //     last = list.get(0).getStripe();
  //   }
  //   return ((Long) (Long.valueOf(last) + 1)).toString();
  // }

  public Transaction getTransactionByPaymentIntent(String stripe) {
    return transactionRepository.findByPaymentIntent(stripe).orElseThrow(() -> new NotFoundException("transaction-not-found"));
  }

  public String getNextInvoiceId(ZonedDateTime now) {
    Sort sort = JpaSort.unsafe(Sort.Direction.DESC, "paidAt");
    Pageable pageable = PageRequest.of(0, 1, sort);
    String last = "0";
    List<Transaction> list = transactionRepository.getLastInvoiceId(pageable);
    if (list.size() > 0) {
      Transaction transaction = list.get(0);
      int yearNow = now.withZoneSameInstant(ZoneId.of("Europe/Bucharest")).getYear();
      int yearFromDb = transaction.getPaidAt().withZoneSameInstant(ZoneId.of("Europe/Bucharest")).getYear();
      if (yearNow == yearFromDb) {
        last = transaction.getInvoiceId();
      }
    }
    return ((Long) (Long.valueOf(last) + 1)).toString();
  }

  public Transaction save(Transaction transaction) {
    return transactionRepository.save(transaction);
  }

  public Page<TransactionDto> getAllTransactions(
    List<Long> userIds,
    Pageable pageable,
    String filterByName,
    PaymentType paymentType) {
  var transactions = transactionRepository.findAllByData(
      userIds,
      userIds == null ? 0 : userIds.size(),
      // filterByName,
      paymentType,
      pageable);
  var list = transactions.getContent().stream().map(transaction -> {
    return getTransaction(transaction);}).collect(Collectors.toList());

    return new PageImpl<>(list, pageable, transactions.getTotalElements());
  }
  public Transaction findById(Long id) {
    return transactionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("transaction-not-found"));
  }


}
