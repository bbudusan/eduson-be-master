package com.servustech.eduson.features.permissions.subscriptions;

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
@Table(name = "periods")
public class Period {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

  private String name;
  private String description;
  @Enumerated(EnumType.STRING)
	@Column(name = "interva_l")
  private Interval interval;
  private Long intervalCount;
}
