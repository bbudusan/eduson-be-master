package com.servustech.eduson.features.categories.modules;

import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.products.courses.Course;
import com.servustech.eduson.features.products.courses.Course4Check;
import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.features.products.webinars.Webinar4Check;
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
public class ModuleC4Check implements Benefit4Check {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinTable(name = "course_modules", joinColumns = @JoinColumn(name = "module_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
	private List<Course4Check> courses;

	public boolean isOrHas(Benefit4Check benefit, PermissionsService ps) {
		if ((benefit instanceof ModuleC4Check || benefit instanceof ModuleW4Check || benefit instanceof ModuleE4Check) && benefit.getId().equals(id)) {
			return true;
		}
		if (!(benefit instanceof Course4Check) && !(benefit instanceof Webinar4Check) && !(benefit instanceof LiveEvent4Check)) {
			return false;
		}

		if ((benefit instanceof Course4Check) && this.courses.stream().anyMatch(course -> course.isOrHas(benefit, ps))) {
			return true;
		}
		ModuleW4Check moduleW4Check = ps.getModuleW4Check(id);
		if (moduleW4Check.isOrHas(benefit, ps)) {
			return true;
		}
		ModuleE4Check moduleE4Check = ps.getModuleE4Check(id);
		if (moduleE4Check.isOrHas(benefit, ps)) {
			return true;
		}
		return false;
	}

}
