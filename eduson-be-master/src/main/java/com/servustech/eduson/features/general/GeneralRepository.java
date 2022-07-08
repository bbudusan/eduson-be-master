package com.servustech.eduson.features.general;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface GeneralRepository extends JpaRepository<General, Long> {
	Page<General> findAllIdAndConfidentialAndKeyAndVersionAndLangCodeByKeyContainingAndLangCodeContaining(String filterByName, String langCode, Pageable pageable); // TODO do not return content here
	Page<General> findAllIdAndConfidentialAndKeyAndVersionAndLangCodeByKeyContainingAndLangCodeContainingAndConfidential(String filterByName, String langCode, Pageable pageable, boolean confidential); // TODO do not return content here
  Optional<General> findFirstByKeyAndLangCodeOrderByVersionDesc(String key, String langCode);
  Optional<General> findFirstByKeyAndLangCodeAndConfidentialOrderByVersionDesc(String key, String langCode, boolean confidential);
  Optional<General> findByKeyAndVersionAndLangCode(String key, Long version, String langCode);
	@Modifying
  void deleteAllByKey(String key);
	@Modifying
  void deleteAllByKeyAndVersion(String key, Long version);
	@Modifying
  void deleteAllByKeyAndVersionAndLangCode(String key, Long version, String langCode);

}
