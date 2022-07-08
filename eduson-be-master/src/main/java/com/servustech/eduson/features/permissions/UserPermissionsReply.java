package com.servustech.eduson.features.permissions;
import com.servustech.eduson.features.permissions.permissions.PermissionDto;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserPermissionsReply {
  Page<TransactionDto> page;
  Boolean notPaidYet;
}
