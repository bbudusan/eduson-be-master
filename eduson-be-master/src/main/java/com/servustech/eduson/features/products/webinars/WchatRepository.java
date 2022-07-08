package com.servustech.eduson.features.products.webinars;

import com.servustech.eduson.features.products.webinars.dto.WchatDto;
import com.servustech.eduson.features.products.webinars.dto.Wchat2Dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WchatRepository extends JpaRepository<WchatMessage, Long> {
	List<WchatMessage> findAllByDestAndProductId(String dest, Long productId);
	List<WchatMessage> findAllByDestAndSession(String dest, String session);
	@Query("SELECT new com.servustech.eduson.features.products.webinars.dto.WchatDto(MIN(ch.timestamp), MAX(ch.timestamp), ch.session) from WchatMessage ch " +
		"where ch.dest = :dest AND ch.session IS NOT NULL GROUP BY ch.session")
	Page<WchatDto> findFirstByDestAndSessionIsNotNullGroupBySessionOrderByTimestamp(
		@Param("dest") String dest, Pageable pageable
	);
	@Query("SELECT new com.servustech.eduson.features.products.webinars.dto.Wchat2Dto(ch.productId, u.username, MIN(ch.timestamp), MAX(ch.timestamp)) from WchatMessage ch JOIN ch.sender u " +
		"where ch.dest = :dest AND ch.productId = u.id AND ch.session IS NULL GROUP BY ch.productId")
	Page<Wchat2Dto> findFirstByDestAndSessionIsNullGroupByProductIdOrderByTimestamp(
		@Param("dest") String dest, Pageable pageable
	);
	List<WchatMessage> findAllByDestAndProductIdAndHidden(String dest, Long productId, Boolean hidden);
}
