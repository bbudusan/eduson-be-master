package com.servustech.eduson.features.products.courses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.ZonedDateTime;

@Repository
public interface AdvertRepository extends JpaRepository<Advert, Long> {

	@Query("SELECT a FROM Advert a where a.name like CONCAT('%', :filterByName, '%') OR :filterByName IS NULL")
	Page<Advert> findAllAndFilterByName(@Param("filterByName") String filterByName, Pageable pageable);

	@Query("SELECT ca FROM Course c JOIN c.adverts ca WHERE c.id = :courseId AND (ca.advert.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<CourseAdvert> findAdvertsByCourseAndFilterByName(@Param("courseId") Long courseId,
			@Param("filterByName") String filterByName, Pageable pageable);

	@Query("SELECT a FROM Advert a WHERE a.id NOT IN (SELECT ca.advert.id FROM Course c JOIN c.adverts ca WHERE c.id = :courseId) AND (a.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<Advert> findAdvertsNotInCourseAndFilterByName(@Param("courseId") Long courseId,
			@Param("filterByName") String filterByName, Pageable pageable);

	Page<Advert> findByIdIn(List<Long> ids, Pageable pageable);

	@Query("SELECT ca FROM Advert a JOIN a.courses ca WHERE a.id = :id")
	List<CourseAdvert> findFirstCourse(@Param("id") Long id, Pageable pageable);
}
