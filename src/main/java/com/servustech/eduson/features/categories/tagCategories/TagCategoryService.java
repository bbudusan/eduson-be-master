package com.servustech.eduson.features.categories.tagCategories;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.exceptions.AlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class TagCategoryService {

    private final TagCategoryRepository tagCategoryRepository;

    public TagCategory create(String name) {
        checkNameExisting(name);
        var tagCategory = TagCategory
                .builder()
                .name(name)
                .build();
        return tagCategoryRepository.save(tagCategory);
    }

    public void deleteTagCategory(Long id) {
        var tagCategory = findById(id);
        tagCategoryRepository.delete(tagCategory);
    }

    public TagCategory findById(Long tagCategoryId) {
        return tagCategoryRepository.findById(tagCategoryId)
                .orElseThrow(() -> new NotFoundException("tagcat-w-id-not-exist"));
    }

    @Transactional
    public TagCategory update(Long tagCategoryId, String name) {
        var tagCategory = findById(tagCategoryId);
        tagCategory.setName(name);
        return tagCategory;
    }

    public Page<TagCategory> getAllTagCategories(Pageable pageable) {
        return tagCategoryRepository.findAll(pageable);
    }

    private void checkNameExisting(String name) {
        tagCategoryRepository.findByName(name).ifPresent(s -> {
            throw new AlreadyExistsException("tagcat-w-id-not-exist");
        });
    }

}
