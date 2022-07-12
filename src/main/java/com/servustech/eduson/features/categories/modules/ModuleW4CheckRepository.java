package com.servustech.eduson.features.categories.modules;

import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.features.products.courses.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ModuleW4CheckRepository extends JpaRepository<ModuleW4Check, Long> {

}
