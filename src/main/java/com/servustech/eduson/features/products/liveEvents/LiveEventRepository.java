package com.servustech.eduson.features.products.liveEvents;

import com.servustech.eduson.features.account.User;
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
public interface LiveEventRepository extends JpaRepository<LiveEvent, Long> {
	@Query("SELECT e FROM LiveEvent e where e.name like CONCAT('%', :filterByName, '%') OR :filterByName IS NULL")
	Page<LiveEvent> findAllAndFilterByName(@Param("filterByName") String filterByName, Pageable pageable);
	@Query("SELECT e FROM LiveEvent e where (e.published IS NULL OR e.published = TRUE) AND (e.name like CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<LiveEvent> findAllByPublishedAndFilterByName(@Param("filterByName") String filterByName, Pageable pageable);
	
	Page<LiveEvent> findByIdIn(List<Long> ids, Pageable pageable);
	Page<LiveEvent> findByIdInAndEndTimeGreaterThanOrderByStartTimeAsc(List<Long> ids, ZonedDateTime now, Pageable pageable);

	@Query("SELECT DISTINCT le FROM LiveEvent le JOIN le.coordinators c LEFT OUTER JOIN le.lectors l JOIN le.tags t WHERE (le.published IS NULL OR le.published = TRUE) AND ((le.name like CONCAT('%', :filterByName, '%') OR "
			+
			"le.description like CONCAT('%', :filterByName, '%') OR " +
			"CONCAT(c.firstName, ' ', c.lastName) LIKE CONCAT('%', :filterByName, '%') OR " + 
			"CONCAT(l.firstName, ' ', l.lastName) LIKE CONCAT('%', :filterByName, '%') OR " +
			"t.name LIKE CONCAT('%', :filterByName, '%')) AND (COALESCE(:idsp) IS NULL OR (t.id IN :idsp)) AND (COALESCE(:idsn) IS NULL OR (t.id NOT IN :idsn)))")
	Page<LiveEvent> searchBy(@Param("idsp") List<Long> idsp, @Param("idsn") List<Long> idsn, @Param("filterByName") String filterByName, Pageable pageable);
	Long countByCoordinators(@Param("coordinator") User coordinator);
	Long countByLectors(@Param("lector") User lector);

	@Query("SELECT m FROM LiveEvent le JOIN le.modules m WHERE le.id = :id")
	List<Module> findFirstModule(@Param("id") Long id, Pageable pageable);
	// TODO remove from shopping cart if we delete a product!!
	@Query("SELECT p FROM Permission p WHERE p.productId = :id AND p.productType = 'LIVE_EVENT' AND p.active = TRUE AND " +
		"(p.expires IS NULL OR p.expires > NOW()) AND " + 
		"(p.endsAt IS NULL OR p.endsAt > NOW())"
	)
	List<Permission> findFirstPermission(@Param("id") Long id, Pageable pageable);
	@Query("SELECT s FROM Subscription s JOIN s.subscriptionProducts sp WHERE sp.productId = :id AND sp.productType = 'LIVE_EVENT'"
	)
	List<Subscription> findFirstSubscription(@Param("id") Long id, Pageable pageable);

	@Query("SELECT e FROM LiveEvent e where " +
			"e.published IS NULL OR e.published = TRUE")
	Page<LiveEvent> findAllByPublished(Pageable pageable);
}
