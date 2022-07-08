package com.servustech.eduson.features.products.liveEvents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.Benefit;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.categories.modules.Module;
import com.servustech.eduson.features.categories.tags.Tag;
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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "live_events")
public class LiveEvent implements Benefit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private float price;

	@OneToMany
	@JoinTable(name = "live_event_modules", joinColumns = @JoinColumn(name = "live_event_id"), inverseJoinColumns = @JoinColumn(name = "module_id"))
	private List<Module> modules;

	@Lob
	private String description;

	private Integer credits;

	private Boolean published;
	public boolean getPublished() {
		return published == null || published;
	}

	@OneToOne
	@JoinColumn(name = "file_image_id", nullable = false)
	private File imageFile;

	@OneToMany
	@JoinTable(name = "live_event_tags", joinColumns = @JoinColumn(name = "live_event_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<Tag> tags;

	private ZonedDateTime startTime;

	private ZonedDateTime endTime;

	@CreatedDate
	private ZonedDateTime addedDate;

	@OneToOne
	@JoinColumn(name = "admin_id", nullable = false)
	@JsonIgnore
	private User admin;

	private String stripe;
	private String priceStripe;

	private String session;

	@ManyToMany
	@Fetch(value = FetchMode.SELECT)
	@JoinTable(name = "live_events_coordinators", joinColumns = @JoinColumn(name = "live_event_id"), inverseJoinColumns = @JoinColumn(name = "coordinator_id"))
	private List<User> coordinators;

	@ManyToMany
	@Fetch(value = FetchMode.SELECT)
	@JoinTable(name = "live_events_lectors", joinColumns = @JoinColumn(name = "live_event_id"), inverseJoinColumns = @JoinColumn(name = "lector_id"))
	private List<User> lectors;

	@ManyToMany
	@Fetch(value = FetchMode.SELECT)
	@JoinTable(name = "live_event_favorites", joinColumns = @JoinColumn(name = "live_event_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
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
