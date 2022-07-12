package com.servustech.eduson.features.account.lectors.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LectorViewResponse {
	private Long id;
	private Long userId;
	private String fullName;
	private String email;
	private boolean hasAccess;
	private Boolean published;
}
