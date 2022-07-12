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
@Table(name = "user_legal")
public class Legal {
	
	@Id
	private Long userId;
	private String company;
	private String cui;
	private String regCom;
	private String iban;
  private String address;
  private String country;
  private String county;
  private String city;
  private String zipCode;
  private String phone;
}
