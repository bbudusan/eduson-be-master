package com.servustech.eduson.features.products.webinars;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebinarCoordinatorRepository extends JpaRepository<WebinarCoordinator, WebinarCoordinatorId> {

    @Query("SELECT COUNT(*) FROM WebinarCoordinator wc where wc.coordinator.id = :lectorId")
    Long webinarCountOfCoordinator(@Param("lectorId") Long lectorId);
}
