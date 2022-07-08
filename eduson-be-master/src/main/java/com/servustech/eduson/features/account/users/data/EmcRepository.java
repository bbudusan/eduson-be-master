package com.servustech.eduson.features.account.users.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmcRepository extends JpaRepository<Emc, Long> {
    Optional<Emc> findById(Long id);
}
