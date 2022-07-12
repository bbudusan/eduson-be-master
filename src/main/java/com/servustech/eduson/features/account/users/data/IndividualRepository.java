package com.servustech.eduson.features.account.users.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IndividualRepository extends JpaRepository<Individual, Long> {
    Optional<Individual> findById(Long id);
}
