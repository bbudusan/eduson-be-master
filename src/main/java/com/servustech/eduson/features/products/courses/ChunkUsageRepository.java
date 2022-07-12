package com.servustech.eduson.features.products.courses;

import com.servustech.eduson.features.products.courses.dto.ChunkUsageResponse;
import com.servustech.eduson.features.products.courses.dto.GetUsageParams;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.ZonedDateTime;

@Repository
public interface ChunkUsageRepository extends JpaRepository<ChunkUsage, Long> {
  @Query("select new com.servustech.eduson.features.products.courses.dto.ChunkUsageResponse(cu.id, cu.point, cu.actionId, cu.user.id, cu.user.username, cu.chunk.id, cu.chunk.webinarId, cu.chunk.courseId, cu.chunk.advertId, cu.chunk.number, cu.chunk.type, cu.chunk.quality, cu.chunk.duration2) "+
  "from ChunkUsage cu "+
  "where "+
  "(:#{#p.startDate} IS NULL OR cu.point >= :#{#p.startDate}) AND "+
  "(:#{#p.endDate} IS NULL OR cu.point < :#{#p.endDate}) AND "+
  "(COALESCE(:#{#p.userIds}) IS NULL OR cu.user.id in :#{#p.userIds}) AND "+
  "(:#{#p.streamType} IS NULL OR cu.chunk.type = :#{#p.streamType}) AND "+
  "(:#{#p.idAfter} IS NULL OR cu.id > :#{#p.idAfter}) "
  )
	List<ChunkUsageResponse> getChunkUsages(@Param("p") GetUsageParams getUsageParams, Pageable pageable);
}
