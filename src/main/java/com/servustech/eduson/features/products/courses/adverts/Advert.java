package com.servustech.eduson.features.products.courses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.Benefit;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.categories.modules.Module;
import com.servustech.eduson.features.categories.tags.Tag;
import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.products.courses.CourseAdvert;
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
import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "ads")
public class Advert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title")
	private String name;

	@OneToMany(mappedBy = "advert", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CourseAdvert> courses = new ArrayList<>();

	@OneToOne
	@JoinColumn(name = "file_id", nullable = false)
	private File file;

	private Time duration;

	@Lob
	private String description;

	private String onclick;

}
