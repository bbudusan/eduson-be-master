package com.servustech.eduson.features.general;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GeneralDto {
	private Long id;
	private Boolean confidential;
	private String key;
	private Long version;
	private String langCode;
	private String content;
}
