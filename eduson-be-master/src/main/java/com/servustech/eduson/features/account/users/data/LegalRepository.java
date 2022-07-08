package com.servustech.eduson.features.account.users.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LegalRepository extends JpaRepository<Legal, Long> {
    Optional<Legal> findById(Long id);
}
