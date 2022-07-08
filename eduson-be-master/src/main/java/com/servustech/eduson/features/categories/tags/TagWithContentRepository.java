package com.servustech.eduson.features.categories.tags;

import com.servustech.eduson.features.categories.tagCategories.TagCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagWithContentRepository extends JpaRepository<TagWithContent, Long> {
	
}
