package com.servustech.eduson.features.products.webinars.dto;

import java.time.ZonedDateTime;

import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WchatDto {
  // public WchatDto(
  //   ZonedDateTime startDate,
  //   ZonedDateTime endDate,
  //   String session
  // ) {
  //   this.startDate = startDate;
  //   this.endDate = endDate;
  //   this.session = session;
  // }
//  @Id
//  private Long productId;
//  private String dest;
//  private Long senderId;
//  private String message;
  private ZonedDateTime startDate;
  private ZonedDateTime endDate;
//  private Boolean hidden;
  private String session;

//  private String senderName;
}
