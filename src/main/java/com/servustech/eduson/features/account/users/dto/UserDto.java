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
public class UserDto {
  private Long id;
  @NotBlank
  @Size(min = 2, max = 40)
	private String firstName;
  @NotBlank
  @Size(min = 2, max = 40)
	private String lastName;
  @NotBlank
  @Size(min = 3, max = 15)
  private String username;
  @NotBlank
  @Size(max = 40)
  @Email
	private String email;
  // TODO password check only if exists!!!
  // @NotBlank
  // @Size(min = 8, max = 20)
  private String password;
  private boolean sendWelcomeEmail;

  // TODO change role as well
}
