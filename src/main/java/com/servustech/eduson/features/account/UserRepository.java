package com.servustech.eduson.features.account;

import com.servustech.eduson.features.account.role.Role;
import com.servustech.eduson.features.account.role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
    Optional<User> findByStripe(String stripe);

    List<User> findAllByRoles(Role role);

    Page<User> findAllByRolesAndDataContains(Role role, String firstName, Pageable pageable);

    // @Query("SELECT u FROM User u where (u.name like CONCAT('%', :filterByName,
    // '%') OR :filterByName IS NULL) AND u.roles.")
    // Page<User> findAllAndFilterByNameAndFilterByRole(@Param("filterByName")
    // String filterByName, @Param("role") Long role, Pageable pageable);

    @Query("SELECT l FROM User l where CONCAT(l.firstName, ' ', l.lastName, ' ', l.username, ' ', l.email) like CONCAT('%', :filterByName, '%') "
            +
            " OR :filterByName IS NULL")
    Page<User> findAllAndFilterByName(@Param("filterByName") String filterByName, Pageable pageable);

    Optional<User> findById(Long id);

}
