package com.servustech.eduson.features.products.webinars;

import com.servustech.eduson.features.account.User;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface FavoriteWebinarRepository extends JpaRepository<FavoriteWebinar, FavoriteWebinarId> {
  Page<FavoriteWebinar> findAllByUserAndWebinarNameContainsOrderByWebinarAddedDateDesc(User user, String filterByName, Pageable pageable);
}
