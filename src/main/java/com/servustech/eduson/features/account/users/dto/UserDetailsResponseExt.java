package com.servustech.eduson.features.account.users.dto;

import com.servustech.eduson.features.account.role.Role;
import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.AccountStatus;
import com.servustech.eduson.features.account.lectors.Lector;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDetailsResponseExt {
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private String fullName;
  private Long id;
  private AccountStatus accountStatus;
  private boolean banned;
  private boolean active;
  private boolean inactive;
  private boolean locked;
  private File profileImage;
  private Set<Role> roles;
  private Lector lector;
}
