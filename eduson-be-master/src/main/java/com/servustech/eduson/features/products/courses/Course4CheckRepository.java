package com.servustech.eduson.features.products.courses;

import com.servustech.eduson.features.products.webinars.WebinarCourse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Course4CheckRepository extends JpaRepository<Course4Check, Long> {

	@Query("SELECT c FROM Course c where c.lector.id= :lectorId")
	List<Course4Check> findAllByLector(@Param("lectorId") Long lectorId);

	List<Course4Check> findAllByNameContains(@Param("filterByName") String filterByName);

}
