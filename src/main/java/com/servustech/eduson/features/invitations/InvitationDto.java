package com.servustech.eduson.features.invitation;

import com.servustech.eduson.features.files.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import javax.validation.constraints.Email;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InvitationDto {
  private Long id;
  private Long invitedBy;
  @Size(max = 40)
  @Email
  private String email;
  private String status;
  private Long userId;
  private String fullName;
  private File file;
  private String invitedByFullName;
}
