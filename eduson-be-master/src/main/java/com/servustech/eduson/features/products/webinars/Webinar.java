package com.servustech.eduson.features.products.webinars;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.Benefit;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.categories.modules.Module;
import com.servustech.eduson.features.categories.tags.Tag;
import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.products.courses.Course;
import com.servustech.eduson.features.products.webinars.dto.IdAndOrder;
import com.amazonaws.services.kms.model.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "webinars")
public class Webinar implements Benefit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private float price;

	@OneToMany
	@JoinTable(name = "webinar_modules", joinColumns = @JoinColumn(name = "webinar_id"), inverseJoinColumns = @JoinColumn(name = "module_id"))
	private List<Module> modules;

	private Integer credits;

	private String acronym;

	@Lob
	private String description;

	@OneToOne
	@JoinColumn(name = "file_image_id", nullable = false)
	private File imageFile;

	@OneToMany
	@JoinTable(name = "webinar_tags", joinColumns = @JoinColumn(name = "webinar_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<Tag> tags;

	private ZonedDateTime startTime;

	private ZonedDateTime endTime;

	@CreatedDate
	private ZonedDateTime addedDate;

	private Boolean published;
	public boolean getPublished() {
		return published == null || published;
	}

	@OneToOne
	@JoinColumn(name = "admin_id", nullable = false)
	// @JsonIgnore
	private User admin;

	private String stripe;
	private String priceStripe;

	@OneToMany(mappedBy = "webinar", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("place ASC")
	private List<WebinarCourse> courses = new ArrayList<>();

	public void addCourse(Course course, Long place) {
		WebinarCourse webinarCourse = new WebinarCourse(this, course, place);
		courses.add(webinarCourse);
		course.getWebinars().add(webinarCourse);
	}

	public void removeCourse(Course course) {
		for (Iterator<WebinarCourse> iterator = courses.iterator(); iterator.hasNext();) {
			WebinarCourse webinarCourse = iterator.next();
			if (webinarCourse.getWebinar().equals(this) && webinarCourse.getCourse().equals(course)) {
				iterator.remove();
				webinarCourse.getCourse().getWebinars().remove(webinarCourse);
				webinarCourse.setWebinar(null);
				webinarCourse.setCourse(null);
			}
		}
	}

	@ManyToMany
	@Fetch(value = FetchMode.SELECT) // TODO do we really need the FetchMode.SELECT? why i introduced this? (at live events, could not delete othervise, foreign key)
	@JoinTable(name = "webinar_coordinators", joinColumns = @JoinColumn(name = "webinar_id"), inverseJoinColumns = @JoinColumn(name = "coordinator_id"))
	private List<User> coordinators;

	public void saveCoursesPlace(List<IdAndOrder> coursesPlace) {
		for (IdAndOrder coursePlace : coursesPlace) {
			var course = this.courses.stream()
					.filter(c -> c.getCourse().getId().equals(coursePlace.getId()))
					.findAny()
					.orElseThrow(() -> new NotFoundException("course-w-id-not-exist"));
			course.setPlace(coursePlace.getOrderIndex());
		}
	}

	@ManyToMany
	@Fetch(value = FetchMode.SELECT)
	@JoinTable(name = "webinar_favorites", joinColumns = @JoinColumn(name = "webinar_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<User> favorited;

	public void removeFromFavorites(User user) {
		this.favorited.remove(user);
	}

	public void addToFavorites(User user) {
		this.favorited.add(user);
	}

	public Boolean isFavorited(User user) {
		if (user == null) {
			return null;
		}
		return new Boolean(this.favorited.contains(user));
	}

	public Float getAmount(Long periodId) {
		if (periodId == null) {
			return this.getPrice();
		} else {
			return null;
		}
	}
}
