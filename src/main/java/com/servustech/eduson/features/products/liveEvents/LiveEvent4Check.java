package com.servustech.eduson.features.products.liveEvents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.Benefit4Check;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.categories.modules.Module;
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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "live_events")
public class LiveEvent4Check implements Benefit4Check {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private ZonedDateTime startTime;

	private ZonedDateTime endTime;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SELECT)
	@JoinTable(name = "live_events_coordinators", joinColumns = @JoinColumn(name = "live_event_id"), inverseJoinColumns = @JoinColumn(name = "coordinator_id"))
	private List<User> coordinators;

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SELECT)
	@JoinTable(name = "live_events_lectors", joinColumns = @JoinColumn(name = "live_event_id"), inverseJoinColumns = @JoinColumn(name = "lector_id"))
	private List<User> lectors;

	public boolean isOrHas(Benefit4Check benefit, PermissionsService ps) {
		if (benefit instanceof LiveEvent4Check && benefit.getId().equals(id)) {
			return true;
		}
		return false;
	}
}
