package com.servustech.eduson.features.invitation;

import com.servustech.eduson.features.account.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

  Page<Invitation> findAllByInvitedBy(User user, Pageable pageable);
  Page<Invitation> findAllByUser(User user, Pageable pageable);

}
