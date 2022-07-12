package com.servustech.eduson.features.products.courses;

import com.servustech.eduson.features.products.webinars.WebinarCourse;
import com.servustech.eduson.features.categories.modules.Module;
import com.servustech.eduson.features.permissions.permissions.Permission;
import com.servustech.eduson.features.permissions.subscriptions.Subscription;
import com.servustech.eduson.features.categories.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.ZonedDateTime;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

	@Query("SELECT c FROM Course c where c.name like CONCAT('%', :filterByName, '%') OR :filterByName IS NULL")
	Page<Course> findAllAndFilterByName(@Param("filterByName") String filterByName, Pageable pageable);

	@Query("SELECT c FROM Course c where (c.name like CONCAT('%', :filterByName, '%') OR :filterByName IS NULL) AND c.lector.id = :lectorId AND " +
			"(c.publishedDate IS NULL OR c.publishedDate < :now) AND (c.published IS NULL OR c.published = TRUE) ORDER BY c.addedDate DESC")
	Page<Course> findCoursesByLectorAndFilterByName(@Param("now") ZonedDateTime now, @Param("lectorId") Long lectorId,
			@Param("filterByName") String filterByName, Pageable pageable);

	@Query("SELECT COUNT(*) FROM Course c where c.lector.id = :lectorId AND (c.publishedDate IS NULL OR c.publishedDate < NOW()) AND (c.published IS NULL OR c.published = TRUE)")
	Long courseCountOfLector(@Param("lectorId") Long lectorId);

	@Query("SELECT wc FROM Webinar w JOIN w.courses wc WHERE w.id = :webinarId AND (wc.course.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<WebinarCourse> findCoursesByWebinarAndFilterByName(@Param("webinarId") Long webinarId,
			@Param("filterByName") String filterByName, Pageable pageable);

	@Query("SELECT c FROM Course c WHERE c.id NOT IN (SELECT wc.course.id FROM Webinar w JOIN w.courses wc WHERE w.id = :webinarId) AND (c.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<Course> findCoursesNotInWebinarAndFilterByName(@Param("webinarId") Long webinarId,
			@Param("filterByName") String filterByName, Pageable pageable);

	Page<Course> findByIdIn(List<Long> ids, Pageable pageable);

	@Query("SELECT DISTINCT c FROM Course c LEFT OUTER JOIN c.tags t WHERE (c.publishedDate IS NULL OR c.publishedDate < NOW()) AND (c.published IS NULL OR c.published = TRUE) AND " +
			"((c.name like CONCAT('%', :filterByName, '%') OR " +
			"c.description like CONCAT('%', :filterByName, '%') OR " +
			"CONCAT(c.lector.firstName, ' ', c.lector.lastName) LIKE CONCAT('%', :filterByName, '%') OR " +
			"t.name LIKE CONCAT('%', :filterByName, '%')) AND (COALESCE(:idsp) IS NULL OR (t.id IN :idsp)) AND (COALESCE(:idsn) IS NULL OR (t.id NOT IN :idsn)))")
	Page<Course> searchBy(@Param("idsp") List<Long> idsp, @Param("idsn") List<Long> idsn, @Param("filterByName") String filterByName, Pageable pageable);

	@Query("SELECT c FROM Course c where " +
			"(c.publishedDate IS NULL OR c.publishedDate < :now) AND (c.published IS NULL OR c.published = TRUE)")
	Page<Course> findAllByPublishedDateIsNullOrPublishedDateBefore(ZonedDateTime now, Pageable pageable);
	@Query("SELECT c FROM Course c where (c.name like CONCAT('%', :filterByName, '%') OR :filterByName IS NULL) AND " +
			"(c.publishedDate IS NULL OR c.publishedDate < :now) AND (c.published IS NULL OR c.published = TRUE)")
	Page<Course> findAllByIPublishedDateIsNullOrPublishedDateBeforeIAndNameContains(ZonedDateTime now, String filterByName, Pageable pageable);

	@Query("SELECT wc FROM Course c JOIN c.webinars wc WHERE c.id = :id")
	List<WebinarCourse> findFirstWebinar(@Param("id") Long id, Pageable pageable);
	@Query("SELECT m FROM Course c JOIN c.modules m WHERE c.id = :id")
	List<Module> findFirstModule(@Param("id") Long id, Pageable pageable);
	// TODO remove from shopping cart if we delete a product!!
	@Query("SELECT p FROM Permission p WHERE p.productId = :id AND p.productType = 'COURSE' AND p.active = TRUE AND " +
		"(p.expires IS NULL OR p.expires > NOW()) AND " + 
		"(p.endsAt IS NULL OR p.endsAt > NOW())"
	)
	List<Permission> findFirstPermission(@Param("id") Long id, Pageable pageable);
	@Query("SELECT s FROM Subscription s JOIN s.subscriptionProducts sp WHERE sp.productId = :id AND sp.productType = 'COURSE'"
	)
	List<Subscription> findFirstSubscription(@Param("id") Long id, Pageable pageable);

	@Query("SELECT DISTINCT t from Course c JOIN c.tags t WHERE c.id IN :ids")
	List<Tag> findDistinctTagsByIdIn(@Param("ids") List<Long> ids);
}
