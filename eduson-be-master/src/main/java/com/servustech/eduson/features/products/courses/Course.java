package com.servustech.eduson.features.products.courses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.Benefit;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.categories.modules.Module;
import com.servustech.eduson.features.categories.tags.Tag;
import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.products.webinars.WebinarCourse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "courses")
public class Course implements Benefit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private Float price;

	@OneToMany
	@JoinTable(name = "course_modules", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "module_id"))
	private List<Module> modules;

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<WebinarCourse> webinars = new ArrayList<>();

	@OneToOne
	@JoinColumn(name = "file_id", nullable = false)
	private File courseFile;

	private Time duration;

	@Lob
	private String description;

	@OneToOne
	@JoinColumn(name = "file_image_id", nullable = false)
	private File imageFile;

	@OneToMany
	@JoinTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<Tag> tags;

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("start ASC, priority ASC")
	private List<CourseAdvert> adverts = new ArrayList<>();

	public void addAdvert(Advert advert, Time start, Integer priority, Long rule) {
		CourseAdvert courseAdvert = new CourseAdvert(this, advert, start, priority, rule);
		adverts.add(courseAdvert);
		advert.getCourses().add(courseAdvert);
	}

	public void removeAdvert(Advert advert) {
		for (Iterator<CourseAdvert> iterator = adverts.iterator(); iterator.hasNext();) {
			CourseAdvert courseAdvert = iterator.next();
			if (courseAdvert.getCourse().equals(this) && courseAdvert.getAdvert().equals(advert)) {
				iterator.remove();
				courseAdvert.getAdvert().getCourses().remove(courseAdvert);
				courseAdvert.setAdvert(null);
				courseAdvert.setCourse(null);
			}
		}
	}
	public void updateAdvert(Advert advert, Time start, Integer priority, Long rule) {
		for (Iterator<CourseAdvert> iterator = adverts.iterator(); iterator.hasNext();) {
			CourseAdvert courseAdvert = iterator.next();
			if (courseAdvert.getCourse().equals(this) && courseAdvert.getAdvert().equals(advert)) {
				courseAdvert.setStart(start);
				courseAdvert.setPriority(priority);
				courseAdvert.setRule(rule);
			}
		}
	}

	@CreatedDate
	private ZonedDateTime addedDate;

	@OneToOne
	@JoinColumn(name = "admin_id", nullable = false)
	private User admin;

	@OneToOne
	@JoinColumn(name = "lector_id", nullable = false)
	private User lector;

	private String stripe;
	private String priceStripe;

	private ZonedDateTime publishedDate;
	private Boolean published;
	public boolean getPublished() {
		return published == null || published;
	}

	@ManyToMany
	@Fetch(value = FetchMode.SELECT)
	@JoinTable(name = "course_favorites", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
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
