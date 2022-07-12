package com.servustech.eduson.features.categories.tags.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TagDto {

    private String name;
    private Long tagCategoryId;
    private Long id;

}
