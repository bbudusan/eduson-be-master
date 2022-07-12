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
public class UserPersonalDataDto {
  @Size(min = 2, max = 40)
  private String firstName;
  @Size(min = 2, max = 40)
  private String lastName;
  @Size(max = 40)
  @Email
  private String email;
}
