package com.servustech.eduson.features.general;

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
@Table(name = "general")
public class General {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private boolean confidential;
	@Column(name = "gkey")
	private String key;
	private Long version;
	private String langCode;
  // @Type(type="org.hibernate.type.StringClobType")
  @Lob
	private String content;
}
