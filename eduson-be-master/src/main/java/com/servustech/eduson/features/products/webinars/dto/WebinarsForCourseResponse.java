package com.servustech.eduson.features.products.webinars.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WebinarsForCourseResponse {

    private Long webinarId;
    private String webinarName;

}
