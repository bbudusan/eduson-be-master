package com.servustech.eduson.features.account.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VatDataDto {
  private String cui;
  private String companyName;
  private String companyAddress;
  private Boolean isValid;
}
