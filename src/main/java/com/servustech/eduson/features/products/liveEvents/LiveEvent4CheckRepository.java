package com.servustech.eduson.features.products.liveEvents;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveEvent4CheckRepository extends JpaRepository<LiveEvent4Check, Long> {
	@Query("SELECT DISTINCT e FROM LiveEvent e JOIN e.coordinators ec JOIN e.lectors el WHERE ec.id = :userId OR el.id = :userId")
	List<LiveEvent4Check> findAllLiveEventsOf(@Param("userId") Long userId);

	List<LiveEvent4Check> findAllByNameContains(@Param("filterByName") String filterByName);
}
