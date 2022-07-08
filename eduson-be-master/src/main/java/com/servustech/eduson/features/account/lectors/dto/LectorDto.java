package com.servustech.eduson.features.account.lectors.dto;

import com.servustech.eduson.features.files.File;

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
public class LectorDto {
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
	// private Long profileImageId; // TODO is this needed here?
  private boolean sendWelcomeEmail;

	private boolean hasAccess;
	private String description;
	private Long titleId;

  private File profileImage;
}
