package com.servustech.eduson.features.categories.tagCategories;

import com.servustech.eduson.features.categories.tags.dto.TagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/tag-category")
@RequiredArgsConstructor
public class TagCategoryController {

    private final TagCategoryService tagCategoryService;

    @PostMapping
    public ResponseEntity<?> createTagCategory(@RequestBody TagCategoryDto tagCategoryDto) {
        return ResponseEntity.ok(tagCategoryService.create(tagCategoryDto.getName()));
    }

    @DeleteMapping("/{tagCategoryId}")
    public void deleteTagCategory(@PathVariable Long tagCategoryId) {
        tagCategoryService.deleteTagCategory(tagCategoryId);
    }

    @GetMapping("/page")
    public ResponseEntity<?> getAllTagsCategory(
        Pageable pageable,
        @RequestParam(name = "unpaged", required = false) boolean unpaged
    ) {
        return ResponseEntity.ok(tagCategoryService.getAllTagCategories(unpaged ? Pageable.unpaged() : pageable));
    }

    @PutMapping("/{tagCategoryId}")
    public ResponseEntity<?> updateTagCategory(@PathVariable Long tagCategoryId, @RequestBody TagDto request) {
        return ResponseEntity.ok(tagCategoryService.update(tagCategoryId, request.getName()));
    }

}
