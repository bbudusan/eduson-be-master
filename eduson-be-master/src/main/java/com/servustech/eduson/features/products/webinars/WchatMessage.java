package com.servustech.eduson.features.products.webinars;

import java.time.ZonedDateTime;

import javax.persistence.*;

import com.servustech.eduson.features.account.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "wchatmessages")
public class WchatMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long productId;
  private String dest;
  @OneToOne
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;
  private String message;
  private ZonedDateTime timestamp;
  private Boolean hidden;
  private String session;
}
