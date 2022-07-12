package com.servustech.eduson.features.account;

import com.servustech.eduson.features.account.role.Role;
import com.servustech.eduson.features.account.role.RoleName;
import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.account.users.data.Emc;
import com.servustech.eduson.features.products.courses.Course;
import com.servustech.eduson.features.products.webinars.Webinar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

import org.springframework.data.domain.Page;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "users", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "username" }),
		@UniqueConstraint(columnNames = { "email" }) })
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 40)
	private String firstName;

	@NotBlank
	@Size(max = 40)
	private String lastName;

	@NotBlank
	private String username;

	@NaturalId(mutable = true)
	@NotBlank
	@Size(max = 40)
	@Email
	private String email;

	@NotBlank
	@Size(max = 100)
	private String password;

	// @OneToOne
	// @JoinTable(name = "user_emc",
	// joinColumns = @JoinColumn(name = "user_id"))
	// private Optional<Emc> emc;

	@OneToOne
	@JoinColumn(name = "profile_image_id") // , nullable = false
	private File profileImage;

	@Enumerated(EnumType.STRING)
	@Column(name = "account_status")
	private AccountStatus accountStatus = AccountStatus.ACTIVE;

	private String stripe;
	private Boolean invoiceAddressPersonal;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	public boolean isAdmin() {
		return roles.stream().filter(role -> role.getName() == RoleName.ROLE_ADMIN).collect(Collectors.toList()).size() > 0;
	}

	public boolean isLector() {
		return roles.stream().filter(role -> role.getName() == RoleName.ROLE_LECTOR).collect(Collectors.toList())
				.size() > 0;
	}

	public boolean isActive() {
		return accountStatus.isActive();
	}

	public boolean isBanned() {
		return accountStatus.isBanned();
	}

	public boolean isInactive() {
		return accountStatus.isInactive();
	}

	public boolean isLocked() {
		return accountStatus.isLocked();
	}

	public boolean isRegisteredOnly() {
		return accountStatus.isRegisteredOnly();
	}

	public String getFullName() {
		return this.getFirstName() + " " + this.getLastName();
	}

	// @ManyToMany
	// @Fetch(value = FetchMode.SELECT)
	// @JoinTable(name = "course_favorites",
	// joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns =
	// @JoinColumn(name = "course_id"))
	// private List<Course> favoritedCourses;
	// @ManyToMany
	// @Fetch(value = FetchMode.SELECT)
	// @JoinTable(name = "webinar_favorites",
	// joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns =
	// @JoinColumn(name = "webinar_id"))
	// private List<Webinar> favoritedWebinars;

	// @Formula("first_name")
	@Formula("CONCAT(first_name, ' ', last_name, ' ', email, ' ', username)")
	private String data;

}
