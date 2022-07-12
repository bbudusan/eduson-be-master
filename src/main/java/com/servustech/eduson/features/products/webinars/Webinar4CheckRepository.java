package com.servustech.eduson.features.products.webinars;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface Webinar4CheckRepository extends JpaRepository<Webinar4Check, Long> {

	@Query("SELECT w FROM Webinar w JOIN w.coordinators wc WHERE wc.id = :userId")
	List<Webinar4Check> findAllWebinarsOf(@Param("userId") Long userId);

	List<Webinar4Check> findAllByNameContains(@Param("filterByName") String filterByName);
}
