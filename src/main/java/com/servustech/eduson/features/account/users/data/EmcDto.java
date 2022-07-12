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
public class EmcDto {

  @NotBlank
	private boolean emc;
  private String grade;
  private String cuim;
  private String specialty;
  private String job;
}
