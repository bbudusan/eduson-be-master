package com.servustech.eduson.features.categories.modules;

import com.servustech.eduson.features.products.courses.Course;

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
@Table(name = "modules")
public class Module {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", unique = true)
	private String name;

	private Float price;
	private String stripe;

	private Boolean published;
	public boolean getPublished() {
		return published == null || published;
	}

	@Formula("(select COUNT(*) from webinars w where (select COUNT(*) from webinar_modules wm where wm.module_id = id AND wm.webinar_id = w.id) > 0)")
	private Long webinarCnt;
	@Formula("(select COUNT(*) from live_events le where (select COUNT(*) from live_event_modules lem where lem.module_id = id AND lem.live_event_id = le.id) > 0)")
	private Long eventCnt;
	@Formula("(select COUNT(*) from courses c where (select COUNT(*) from course_modules cm where cm.module_id = id AND cm.course_id = c.id) > 0 and "
			+
			"(select COUNT(*) from webinars w WHERE " +
			"(select COUNT(*) from webinar_modules wm where wm.module_id = id AND wm.webinar_id = w.id) > 0 AND " +
			"(select COUNT(*) from webinar_courses wc where wc.webinar_id = w.id AND wc.course_id = c.id) > 0 " +
			") = 0)")
	private Long courseCnt;

	public Float getAmount(Long periodId) {
		if (periodId == null) {
			return this.getPrice();
		} else {
			return null;
		}
	}
}
