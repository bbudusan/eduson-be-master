package com.servustech.eduson.features.categories.tags;

import com.servustech.eduson.features.categories.tagCategories.TagCategory;
import com.servustech.eduson.features.products.courses.Course;
import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.features.products.liveEvents.LiveEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Formula;

import javax.persistence.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "tags")
public class TagWithContent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinTable(name = "course_tags", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
	private List<Course> courses;

	public void addToCourses(List<Course> courses) {
		this.courses.addAll(courses);
	}

	public void removeFromCourses(List<Course> courses) {
		this.courses.removeAll(courses);
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinTable(name = "webinar_tags", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "webinar_id"))
	private List<Webinar> webinars;

	public void addToWebinars(List<Webinar> webinars) {
		this.webinars.addAll(webinars);
	}

	public void removeFromWebinars(List<Webinar> webinars) {
		this.webinars.removeAll(webinars);
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinTable(name = "live_event_tags", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "live_event_id"))
	private List<LiveEvent> liveEvents;

	public void addToLiveEvents(List<LiveEvent> liveEvents) {
		this.liveEvents.addAll(liveEvents);
	}

	public void removeFromLiveEvents(List<LiveEvent> liveEvents) {
		this.liveEvents.removeAll(liveEvents);
	}
}
