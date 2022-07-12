package com.servustech.eduson.features.account.lectors;

import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.lectorTitle.LectorTitle;
import com.servustech.eduson.features.files.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "lectors")
public class Lector {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@OneToOne
	@JoinColumn(name = "title_id", nullable = false)
	private LectorTitle title;
	
	private boolean hasAccess;
		
	@Lob
	private String description;

	public String getNameAndTitle(){
		return this.getTitle().getTitle()+ " "+this.getUser().getLastName()+ " " +this.getUser().getFirstName();
	}

	private Boolean published;
	public boolean getPublished() {
		return published == null || published;
	}

}
