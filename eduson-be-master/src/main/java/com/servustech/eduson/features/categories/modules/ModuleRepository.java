package com.servustech.eduson.features.categories.modules;

import com.servustech.eduson.features.products.liveEvents.LiveEvent;
import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.features.products.courses.Course;
import com.servustech.eduson.features.permissions.permissions.Permission;
import com.servustech.eduson.features.permissions.subscriptions.Subscription;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
	
	Optional<Module> findByName(String name);

	Page<Module> findAllByNameContaining(String name, Pageable pageable);
	@Query("SELECT m FROM Module m where (m.published IS NULL OR m.published = TRUE) AND (m.name like CONCAT('%', :name, '%') OR :name IS NULL)")
	Page<Module> findAllByPublishedAndNameContaining(@Param("name") String name, Pageable pageable);

	@Query("select w from LiveEvent w where (select COUNT(*) from LiveEventModule wm where wm.module.id = :id AND wm.liveEvent.id = w.id) > 0"+
	" AND (w.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<LiveEvent> getLiveEvents(@Param("id") Long id, @Param("filterByName") String filterByName, Pageable pageable);
	@Query("select w from LiveEvent w where (select COUNT(*) from LiveEventModule wm where wm.module.id = :id AND wm.liveEvent.id = w.id) = 0"+
	" AND (w.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<LiveEvent> getLiveEventsUnassigned(@Param("id") Long id, @Param("filterByName") String filterByName, Pageable pageable);
	@Query("select w from Webinar w where (select COUNT(*) from WebinarModule wm where wm.module.id = :id AND wm.webinar.id = w.id) > 0"+
	" AND (w.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<Webinar> getWebinars(@Param("id") Long id, @Param("filterByName") String filterByName, Pageable pageable);
	@Query("select w from Webinar w where (select COUNT(*) from WebinarModule wm where wm.module.id = :id AND wm.webinar.id = w.id) = 0"+
	" AND (w.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<Webinar> getWebinarsUnassigned(@Param("id") Long id, @Param("filterByName") String filterByName, Pageable pageable);
	@Query("select c from Course c where (select COUNT(*) from CourseModule cm where cm.module.id = :id AND cm.course.id = c.id) > 0 AND " +
	"(select COUNT(*) from Webinar w WHERE "+
			"(select COUNT(*) from WebinarModule wm where wm.module.id = :id AND wm.webinar.id = w.id) > 0 AND "+
			"(select COUNT(*) from WebinarCourse wc where wc.webinar.id = w.id AND wc.course.id = c.id) > 0 "+
	") = 0"+
	" AND (c.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<Course> getCourses(@Param("id") Long id, @Param("filterByName") String filterByName, Pageable pageable);
	@Query("select c from Course c where (select COUNT(*) from CourseModule cm where cm.module.id = :id AND cm.course.id = c.id) = 0 AND " +
	"(select COUNT(*) from Webinar w WHERE "+
			"(select COUNT(*) from WebinarModule wm where wm.module.id = :id AND wm.webinar.id = w.id) > 0 AND "+
			"(select COUNT(*) from WebinarCourse wc where wc.webinar.id = w.id AND wc.course.id = c.id) > 0 "+
	") = 0"+
	" AND (c.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<Course> getCoursesUnassigned(@Param("id") Long id, @Param("filterByName") String filterByName, Pageable pageable);

	@Query("select cm from CourseModule cm where cm.module.id = :id")
	List<CourseModule> findFirstCourse(@Param("id") Long id, Pageable pageable);
	@Query("select wm from WebinarModule wm where wm.module.id = :id")
	List<WebinarModule> findFirstWebinar(@Param("id") Long id, Pageable pageable);
	@Query("select lem from LiveEventModule lem where lem.module.id = :id")
	List<LiveEventModule> findFirstLiveEvent(@Param("id") Long id, Pageable pageable);
	@Query("SELECT p FROM Permission p WHERE p.productId = :id AND p.productType = 'MODULE' AND p.active = TRUE AND " +
		"(p.expires IS NULL OR p.expires > NOW()) AND " + 
		"(p.endsAt IS NULL OR p.endsAt > NOW())"
	)
	List<Permission> findFirstPermission(@Param("id") Long id, Pageable pageable);
	@Query("SELECT s FROM Subscription s JOIN s.subscriptionProducts sp WHERE sp.productId = :id AND sp.productType = 'MODULE'"
	)
	List<Subscription> findFirstSubscription(@Param("id") Long id, Pageable pageable);
}
