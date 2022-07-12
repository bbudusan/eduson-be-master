package com.servustech.eduson.features.account.lectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface LectorRepository extends JpaRepository<Lector, Long> {

	@Query("SELECT l FROM Lector l where (:priv is TRUE OR l.published is NULL OR l.published is TRUE) AND " +
			"((CONCAT(l.user.firstName, ' ', l.user.lastName, ' ', l.user.email, ' ', l.user.username) like CONCAT('%', :filterByName, '%') "
			+
			"OR :filterByName IS NULL) " +
			"AND (select COUNT(*) from l.user.roles r where :lectorRoleId = r.id) > 0)")
	Page<Lector> findAllAndFilterByName(@Param("filterByName") String filterByName, @Param("priv") Boolean priv, Pageable pageable, @Param("lectorRoleId") Long lectorRoleId);

	@Query("SELECT l FROM Lector l where (l.published is NULL OR l.published is TRUE) AND " +
			"((CONCAT(l.user.firstName, ' ', l.user.lastName, ' ', l.description) like CONCAT('%', :filterByName, '%')) " +
			"AND (select COUNT(*) from l.user.roles r where :lectorRoleId = r.id) > 0)"
			)
	Page<Lector> searchBy(@Param("filterByName") String filterByName, Pageable pageable, @Param("lectorRoleId") Long lectorRoleId);

	Optional<Lector> findByUserId(@Param("userId") Long userId);
	@Query("SELECT l FROM Lector l where (l.published is NULL OR l.published is TRUE) AND l.user.id = :userId")
	Optional<Lector> findByUserIdPub(@Param("userId") Long userId);

	List<Lector> findAllByPublishedIsNullOrPublishedIsTrue();
}
