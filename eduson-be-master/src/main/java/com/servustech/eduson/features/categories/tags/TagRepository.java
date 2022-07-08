package com.servustech.eduson.features.categories.tags;

import com.servustech.eduson.features.products.liveEvents.LiveEvent;
import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.features.products.courses.Course;
import com.servustech.eduson.features.categories.tagCategories.TagCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	
	Optional<Tag> findByName(String name);
	
	Page<Tag> findAllByTagCategoryAndNameContaining(TagCategory tagCategory, String name, Pageable pageable); // TODO
	
	@Query("select w from LiveEvent w where (COALESCE(:eventsn) IS NULL OR w.id not in :eventsn) AND (w.published IS NULL OR w.published = TRUE) AND (select COUNT(*) from LiveEventTag wm where (COALESCE(:idsp) IS NULL OR wm.tag.id in :idsp) AND (COALESCE(:idsn) IS NULL OR wm.tag.id not in :idsn) AND wm.liveEvent.id = w.id) > 0"+
	" AND (w.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<LiveEvent> getLiveEvents(@Param("idsp") List<Long> idsp, @Param("idsn") List<Long> idsn, @Param("eventsn") List<Long> eventsn, @Param("filterByName") String filterByName, Pageable pageable);
	@Query("select w from LiveEvent w where (select COUNT(*) from LiveEventTag wm where wm.tag.id = :id AND wm.liveEvent.id = w.id) = 0"+
	" AND (w.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<LiveEvent> getLiveEventsUnassigned(@Param("id") Long id, @Param("filterByName") String filterByName, Pageable pageable);
	@Query("select w from Webinar w where (COALESCE(:webinarsn) IS NULL OR w.id not in :webinarsn) AND (w.published IS NULL OR w.published = TRUE) AND (select COUNT(*) from WebinarTag wm where (COALESCE(:idsp) IS NULL OR wm.tag.id in :idsp) AND (COALESCE(:idsn) IS NULL OR wm.tag.id not in :idsn) AND wm.webinar.id = w.id) > 0"+
	" AND (w.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<Webinar> getWebinars(@Param("idsp") List<Long> idsp, @Param("idsn") List<Long> idsn, @Param("webinarsn") List<Long> webinarsn, @Param("filterByName") String filterByName, Pageable pageable);
	@Query("select w from Webinar w where (select COUNT(*) from WebinarTag wm where wm.tag.id = :id AND wm.webinar.id = w.id) = 0"+
	" AND (w.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<Webinar> getWebinarsUnassigned(@Param("id") Long id, @Param("filterByName") String filterByName, Pageable pageable);
	@Query("select c from Course c where (COALESCE(:coursesn) IS NULL OR c.id not in :coursesn) AND (c.published IS NULL OR c.published = TRUE) AND (select COUNT(*) from CourseTag cm where (COALESCE(:idsp) IS NULL OR cm.tag.id in :idsp) AND (COALESCE(:idsn) IS NULL OR cm.tag.id not in :idsn) AND cm.course.id = c.id) > 0" +
	" AND (c.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<Course> getCourses(@Param("idsp") List<Long> idsp, @Param("idsn") List<Long> idsn, @Param("coursesn") List<Long> coursesn, @Param("filterByName") String filterByName, Pageable pageable);
	@Query("select c from Course c where (select COUNT(*) from CourseTag cm where cm.tag.id = :id AND cm.course.id = c.id) = 0" +
	" AND (c.name LIKE CONCAT('%', :filterByName, '%') OR :filterByName IS NULL)")
	Page<Course> getCoursesUnassigned(@Param("id") Long id, @Param("filterByName") String filterByName, Pageable pageable);

}
