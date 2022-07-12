package com.servustech.eduson.features.products.courses;

import com.servustech.eduson.security.payload.StreamType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.ZonedDateTime;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, Long> {
	List<Chunk> findAllByWebinarIdAndType(Long webinarId, StreamType type);
	List<Chunk> findAllByCourseIdAndTypeAndWebinarIdIsNull(Long courseId, StreamType type);
	@Query("select max(c.number) from Chunk c where c.webinarId = :id and c.type = :type and c.active = TRUE")
	Long findMaxNumberByWebinarIdAndTypeAndActiveIsTrue(@Param("id") Long id, @Param("type") StreamType type);
	@Query("select max(c.number) from Chunk c where c.courseId = :id and c.type = :type and c.active = TRUE and c.webinarId IS NULL")
	Long findMaxNumberByCourseIdAndTypeAndActiveIsTrueAndWebinarIdIsNull(@Param("id") Long id, StreamType type);
	Chunk findOneByWebinarIdAndTypeAndNumberAndActiveIsTrue(Long webinarId, StreamType type, Long number); // add quality
	Chunk findOneByCourseIdAndTypeAndNumberAndActiveIsTrueAndWebinarIdIsNull(Long courseId, StreamType type, Long number); // add quality
}
