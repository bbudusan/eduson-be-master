package com.servustech.eduson.features.products.courses;

import com.servustech.eduson.features.account.User;
// import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.security.payload.StreamType;
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
import java.util.Iterator;
import java.sql.Time;
import java.time.LocalTime;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "chunks")
public class Chunk {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// @OneToOne
  // @JoinColumn(name = "webinar_id", nullable = true)
	// private Webinar webinar;
	Long webinarId;
	// @OneToOne
  // @JoinColumn(name = "course_id", nullable = true)
	// private Webinar course;
	Long courseId;
	// @OneToOne
  // @JoinColumn(name = "advert_id", nullable = true)
	// private Advert advert;
	Long advertId;

	private Long number;
	@Enumerated(EnumType.STRING)
	private StreamType type;
	private String quality;
	@Column(name = "duration", length = 3)
	private Time duration;
	@Column(name = "duration", columnDefinition = "TIME", insertable=false, updatable=false)
	private LocalTime duration2;
	private Boolean active;

}
