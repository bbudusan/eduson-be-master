package com.servustech.eduson.features.products.courses.dto;

import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.categories.tags.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CoursesView {
    private Long id;
    private String name;
    private File imageFile;
	private List<Tag> tags;
    private Boolean favorited;
}
