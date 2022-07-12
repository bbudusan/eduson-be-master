package com.servustech.eduson.features.general;

import com.servustech.eduson.features.files.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GeneralFileDto {
	private Long id;
	private String key;
	private Long version;
	private File file;
	private Long fileId;
}
