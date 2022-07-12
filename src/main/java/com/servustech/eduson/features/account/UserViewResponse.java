package com.servustech.eduson.features.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserViewResponse {
	
	private Long userId;
	private String name;
	private String email;
	private boolean hasAccess;
	private Boolean isAdmin;
	private Boolean isLector;
}
