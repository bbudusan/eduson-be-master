package com.servustech.eduson.features.categories.tags;

import com.servustech.eduson.features.categories.tagCategories.TagCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Formula;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "tags")
public class Tag {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", unique = true)
	private String name;
	
	@ManyToOne
	private TagCategory tagCategory;

	@Formula("(select COUNT(*) from webinars w where (select COUNT(*) from webinar_tags wm where wm.tag_id = id AND wm.webinar_id = w.id) > 0)")
	private Long webinarCnt;
	@Formula("(select COUNT(*) from live_events le where (select COUNT(*) from live_event_tags lem where lem.tag_id = id AND lem.live_event_id = le.id) > 0)")
	private Long eventCnt;
	@Formula("(select COUNT(*) from courses c where (select COUNT(*) from course_tags cm where cm.tag_id = id AND cm.course_id = c.id) > 0)")
	private Long courseCnt;
	// TODO we should not interrogate these all the time! neither at module./tag
	
}
