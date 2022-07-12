package com.servustech.eduson.features.permissions.subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

@Repository
public interface PeriodRepository extends JpaRepository<Period, Long> {
}
