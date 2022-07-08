package com.servustech.eduson.features.categories.modules;

import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.products.liveEvents.LiveEvent;
import com.servustech.eduson.features.products.liveEvents.LiveEvent4Check;
import com.servustech.eduson.features.permissions.Benefit;
import com.servustech.eduson.features.permissions.Benefit4Check;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "modules")
public class ModuleE4Check implements Benefit4Check {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinTable(name = "live_event_modules", joinColumns = @JoinColumn(name = "module_id"), inverseJoinColumns = @JoinColumn(name = "live_event_id"))
	private List<LiveEvent4Check> liveEvents;

	public boolean isOrHas(Benefit4Check benefit, PermissionsService ps) {
		// TODO module 2 seems to have the same 3rd webinar many times
		if (this.liveEvents.stream().anyMatch(liveEvent -> liveEvent.isOrHas(benefit, ps))) {
			return true;
		}
		return false;
	}

}
