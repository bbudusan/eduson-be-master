package com.servustech.eduson.features.products.webinars;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.Benefit4Check;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.categories.tags.Tag;
import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.products.courses.Course;
import com.servustech.eduson.features.products.courses.Course4Check;
import com.servustech.eduson.features.products.webinars.dto.IdAndOrder;
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
public class Webinar4Check implements Benefit4Check {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private ZonedDateTime startTime;

	private ZonedDateTime endTime;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SELECT)
	@JoinTable(name = "webinar_coordinators", joinColumns = @JoinColumn(name = "webinar_id"), inverseJoinColumns = @JoinColumn(name = "coordinator_id"))
	private List<User> coordinators;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinTable(name = "webinar_courses", joinColumns = @JoinColumn(name = "webinar_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
	private List<Course4Check> courses;
	// TODO into isOrHas. Why doesn't throw multiple bags exception here?

	public boolean isOrHas(Benefit4Check benefit, PermissionsService ps) {
		if (benefit instanceof Webinar4Check && benefit.getId().equals(id)) {
			return true;
		}
		if (!(benefit instanceof Course4Check)) {
			return false;
		}
		// TODO finetune the following using the durations and course order:
		if (this.getStartTime().isAfter(ZonedDateTime.now())) { // TODO plus a general time delay which can be set
			return false;
		}
		return this.courses.stream().anyMatch(course -> course.isOrHas(benefit, ps));
	}

}
