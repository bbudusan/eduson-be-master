package com.servustech.eduson.features.categories.tagCategories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagCategoryRepository extends JpaRepository<TagCategory, Long> {

    Optional<TagCategory> findByName(String name);
}
