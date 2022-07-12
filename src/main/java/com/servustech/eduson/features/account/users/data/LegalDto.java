package com.servustech.eduson.features.account.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LegalDto {

  @NotBlank
  private String company;
  @NotBlank
  private String cui;
  private String regCom;
  private String iban;
  @NotBlank
  private String address;
  @NotBlank
  private String country;
  @NotBlank
  private String county;
  @NotBlank
  private String city;
  @NotBlank
  private String zipCode;
  private String phone;
}
