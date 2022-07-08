package com.servustech.eduson.features.products.courses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.Benefit;
import com.servustech.eduson.features.permissions.Benefit4Check;
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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "courses")
public class Course4Check implements Benefit4Check {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@OneToOne
	@JoinColumn(name = "lector_id", nullable = false)
	private User lector;

	public boolean isOrHas(Benefit4Check benefit, PermissionsService ps) {
		if (benefit instanceof Course4Check && benefit.getId().equals(id)) {
			return true;
		}
		return false;
	}

}
