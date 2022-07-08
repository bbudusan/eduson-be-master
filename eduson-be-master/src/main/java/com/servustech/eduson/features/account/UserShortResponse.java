package com.servustech.eduson.features.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserShortResponse {
	
	private Long id;
	private String name;
	
}
