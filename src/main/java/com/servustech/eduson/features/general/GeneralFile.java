package com.servustech.eduson.features.general;

import com.servustech.eduson.features.files.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "general_files")
public class GeneralFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "gkey")
	private String key;
	private Long version;
	@OneToOne
	@JoinColumn(name = "file_id")
	private File file;
}
