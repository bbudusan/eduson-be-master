package com.servustech.eduson.features.categories.modules;

import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.features.products.webinars.Webinar4Check;
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
public class ModuleW4Check implements Benefit4Check {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinTable(name = "webinar_modules", joinColumns = @JoinColumn(name = "module_id"), inverseJoinColumns = @JoinColumn(name = "webinar_id"))
	private List<Webinar4Check> webinars;

	public boolean isOrHas(Benefit4Check benefit, PermissionsService ps) {
		// TODO module 2 seems to have the same 3rd webinar many times
		if (this.webinars.stream().anyMatch(webinar -> webinar.isOrHas(benefit, ps))) {
			return true;
		}
		return false;
	}

}
