package com.servustech.eduson.features.general;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface GeneralFileRepository extends JpaRepository<GeneralFile, Long> {
	Page<GeneralFile> findAllIdAndKeyAndVersionByKeyContaining(String filterByName, Pageable pageable); // TODO do not return file here
  Optional<GeneralFile> findFirstByKeyOrderByVersionDesc(String key);
  Optional<GeneralFile> findByKeyAndVersion(String key, Long version);
	@Modifying
  void deleteAllByKey(String key);
	@Modifying
  void deleteAllByKeyAndVersion(String key, Long version);

}
