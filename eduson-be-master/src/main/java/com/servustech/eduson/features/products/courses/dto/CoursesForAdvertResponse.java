package com.servustech.eduson.features.products.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CoursesForAdvertResponse {

    private Long id;
    private String name;

}
