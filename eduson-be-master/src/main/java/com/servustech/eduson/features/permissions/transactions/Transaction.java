package com.servustech.eduson.features.permissions.transactions;

import com.servustech.eduson.features.permissions.permissions.Permission;
import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.permissions.permissions.PaymentType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "transaction")
  private List<Permission> permissions;
  // TODO override toString. ex:
//   @Override
// public String toString() {
//     return "Person [id=" + id + ", name=" + name + ", company=" + company + "]";
// }

  private String transactionId;
  private String paymentIntent;

  private ZonedDateTime timestamp;
  private ZonedDateTime paidAt;
  private Float value;
  private String data;
	@OneToOne
	@JoinColumn(name = "invoice_file_id")
  private File invoiceFile;

	@OneToOne
  @JoinColumn(name = "transfer_file_id")
  private File transferFile;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_type")
	private PaymentType paymentType = PaymentType.NOT_NEEDED;

  private String invoiceId;
}
