package com.servustech.eduson.features.products.webinars;

import com.servustech.eduson.features.categories.modules.Module;
import com.servustech.eduson.features.permissions.permissions.Permission;
import com.servustech.eduson.features.permissions.subscriptions.Subscription;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.ZonedDateTime;

@Repository
public interface WebinarRepository extends JpaRepository<Webinar, Long> {

	@Query("SELECT w FROM Webinar w where w.name like CONCAT('%', :filterByName, '%') OR :filterByName IS NULL")
	Page<Webinar> findAllAndFilterByName(@Param("filterByName") String filterByName, Pageable pageable);
	@Query("SELECT w FROM Webinar w where (w.published IS NULL OR w.published = TRUE) AND (w.name like CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<Webinar> findAllByPublishedAndFilterByName(@Param("filterByName") String filterByName, Pageable pageable);

	// @Query("SELECT w FROM Webinar w")
	// List<Webinar>findNew(Pageable pageable);

	Page<Webinar> findByIdIn(List<Long> ids, Pageable pageable);
	Page<Webinar> findByIdInAndEndTimeGreaterThanOrderByStartTimeAsc(List<Long> ids, ZonedDateTime now, Pageable pageable);
	List<Webinar> findByStartTimeGreaterThanOrderByStartTimeAsc(ZonedDateTime now);

	@Query("SELECT DISTINCT w FROM Webinar w JOIN w.coordinators c LEFT OUTER JOIN w.tags t WHERE (w.published IS NULL OR w.published = TRUE) AND ((w.name like CONCAT('%', :filterByName, '%') OR " +
			"w.description like CONCAT('%', :filterByName, '%') OR " +
			"w.acronym like CONCAT('%', :filterByName, '%') OR " +
			"CONCAT(c.firstName, ' ', c.lastName) LIKE CONCAT('%', :filterByName, '%') OR " + 
			"t.name LIKE CONCAT('%', :filterByName, '%')) AND (COALESCE(:idsp) IS NULL OR (t.id IN :idsp)) AND (COALESCE(:idsn) IS NULL OR (t.id NOT IN :idsn)))")
	Page<Webinar> searchBy(@Param("idsp") List<Long> idsp, @Param("idsn") List<Long> idsn, @Param("filterByName") String filterByName, Pageable pageable);

	@Query("SELECT wc FROM Webinar w JOIN w.courses wc WHERE w.id = :id")
	List<WebinarCourse> findFirstCourse(@Param("id") Long id, Pageable pageable);
	@Query("SELECT m FROM Webinar w JOIN w.modules m WHERE w.id = :id")
	List<Module> findFirstModule(@Param("id") Long id, Pageable pageable);
	@Query("SELECT p FROM Permission p WHERE p.productId = :id AND p.productType = 'WEBINAR' AND p.active = TRUE AND " +
		"(p.expires IS NULL OR p.expires > NOW()) AND " + 
		"(p.endsAt IS NULL OR p.endsAt > NOW())"
	)
	List<Permission> findFirstPermission(@Param("id") Long id, Pageable pageable);
	@Query("SELECT s FROM Subscription s JOIN s.subscriptionProducts sp WHERE sp.productId = :id AND sp.productType = 'WEBINAR'"
	)
	List<Subscription> findFirstSubscription(@Param("id") Long id, Pageable pageable);

	@Query("SELECT w FROM Webinar w where " +
			"w.published IS NULL OR w.published = TRUE")
	Page<Webinar> findAllByPublished(Pageable pageable);
}
