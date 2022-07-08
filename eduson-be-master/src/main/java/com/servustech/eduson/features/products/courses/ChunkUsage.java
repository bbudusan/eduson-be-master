package com.servustech.eduson.features.products.courses;

import com.servustech.eduson.features.account.User;
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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "chunk_usages")
public class ChunkUsage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
  @JoinColumn(name = "chunk_id", nullable = true)
	private Chunk chunk;
	@OneToOne
  @JoinColumn(name = "user_id", nullable = true)
	private User user;

	private ZonedDateTime point;
	private Long actionId;

}
