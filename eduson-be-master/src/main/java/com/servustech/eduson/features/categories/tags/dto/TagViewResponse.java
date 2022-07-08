package com.servustech.eduson.features.categories.tags.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TagViewResponse {
	
	private String name;
	private Long id;
	private Long courseCnt;
	private Long webinarCnt;
	private Long eventCnt;
}
