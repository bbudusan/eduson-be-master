package com.servustech.eduson.features.account.users.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user_emc")
public class Emc {
	
	@Id
	private Long userId;
	private boolean emc;
  private String grade;
  private String cuim;
  private String specialty;
  private String job;
  
}
