package com.servustech.eduson.security.payload;

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
public class ConfirmationRequest {
  @NotBlank
  @Size(max = 40)
  @Email
  String email;
  @NotBlank
  String token;
  String password;

}
