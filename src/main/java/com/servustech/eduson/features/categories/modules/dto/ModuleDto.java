package com.servustech.eduson.features.categories.modules.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ModuleDto {
	
	private String name;
	private Boolean published;
}

