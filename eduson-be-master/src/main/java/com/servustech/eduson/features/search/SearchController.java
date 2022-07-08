package com.servustech.eduson.features.search;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import com.servustech.eduson.security.auth.JwtService;
import com.servustech.eduson.security.handler.RequestHandler;
import com.servustech.eduson.security.jwt.JwtTokenProvider;
import com.servustech.eduson.security.userdetails.CustomUserDetailsService;
import com.servustech.eduson.security.constants.AuthConstants;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/search")
public class SearchController {

  private final SearchService searchService;
  private final RequestHandler requestHandler;
  private final JwtService jwtService;

  @GetMapping("/related")
  public ResponseEntity<?> getRelated(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
      @RequestParam(required = false) List<Long> tagsp,
      @RequestParam(required = false) List<Long> tagsn,
      @RequestParam(required = false) List<Long> coursesn,
      @RequestParam(required = false) List<Long> webinarsn,
      @RequestParam(required = false) List<Long> eventsn,
      Pageable pageable) {
    var user = jwtService.getUserFromAuthOk(authToken);
    return ResponseEntity.ok(searchService.getRelated(tagsp, tagsn, coursesn, webinarsn, eventsn, pageable)); // TODO user for favoriting
  }
  @GetMapping("/related/courses")
  public ResponseEntity<?> getRelatedCourses(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
      @RequestParam(required = false) List<Long> tagsp,
      @RequestParam(required = false) List<Long> tagsn, 
      @RequestParam(required = false) List<Long> coursesn,
      @RequestParam(required = false) List<Long> webinarsn,
      @RequestParam(required = false) List<Long> eventsn,
      Pageable pageable) {
    var user = jwtService.getUserFromAuthOk(authToken);
    return ResponseEntity.ok(searchService.getRelatedCourses(tagsp, tagsn, coursesn, pageable));
  }
  @GetMapping("/related/webinars")
  public ResponseEntity<?> getRelatedWebinars(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
      @RequestParam(required = false) List<Long> tagsp,
      @RequestParam(required = false) List<Long> tagsn, 
      @RequestParam(required = false) List<Long> coursesn,
      @RequestParam(required = false) List<Long> webinarsn,
      @RequestParam(required = false) List<Long> eventsn,
      Pageable pageable) {
    var user = jwtService.getUserFromAuthOk(authToken);
    return ResponseEntity.ok(searchService.getRelatedWebinars(tagsp, tagsn, webinarsn, pageable));
  }
  @GetMapping("/related/events")
  public ResponseEntity<?> getRelatedLiveEvents(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
      @RequestParam(required = false) List<Long> tagsp,
      @RequestParam(required = false) List<Long> tagsn, 
      @RequestParam(required = false) List<Long> coursesn,
      @RequestParam(required = false) List<Long> webinarsn,
      @RequestParam(required = false) List<Long> eventsn,
      Pageable pageable) {
    var user = jwtService.getUserFromAuthOk(authToken);
    return ResponseEntity.ok(searchService.getRelatedLiveEvents(tagsp, tagsn, eventsn, pageable));
  }

  @GetMapping("")
  public ResponseEntity<?> publicSearch(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
  @RequestParam(required = false) List<Long> tagsp, @RequestParam(required = false) List<Long> tagsn,
  @RequestParam String filterByName, Pageable pageable) {
    var user = jwtService.getUserFromAuthOk(authToken);
    return ResponseEntity.ok(searchService.publicSearch(tagsp, tagsn, filterByName, pageable, user));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @GetMapping("/")
  ResponseEntity<?> adminSearch(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
  @RequestParam(required = false) List<Long> tagsp, @RequestParam(required = false) List<Long> tagsn,
  @RequestParam String filterByName, Pageable pageable) {
    var user = jwtService.getUserFromAuth(authToken);
    return ResponseEntity.ok(searchService.publicSearch(tagsp, tagsn, filterByName, pageable, user));
  }

  @GetMapping("/courses")
  public ResponseEntity<?> courseSearch(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
  @RequestParam(required = false) List<Long> tagsp, @RequestParam(required = false) List<Long> tagsn,
  @RequestParam String filterByName, Pageable pageable) {
    var user = jwtService.getUserFromAuthOk(authToken);
    return ResponseEntity.ok(searchService.courseSearch(tagsp, tagsn, filterByName, pageable, user));
  }
  @GetMapping("/webinars")
  public ResponseEntity<?> webinarSearch(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
  @RequestParam(required = false) List<Long> tagsp, @RequestParam(required = false) List<Long> tagsn,
  @RequestParam String filterByName, Pageable pageable) {
    var user = jwtService.getUserFromAuthOk(authToken);
    return ResponseEntity.ok(searchService.webinarSearch(tagsp, tagsn, filterByName, pageable, user));
  }
  @GetMapping("/events")
  public ResponseEntity<?> liveEventSearch(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
  @RequestParam(required = false) List<Long> tagsp, @RequestParam(required = false) List<Long> tagsn,
  @RequestParam String filterByName, Pageable pageable) {
    var user = jwtService.getUserFromAuthOk(authToken);
    return ResponseEntity.ok(searchService.liveEventSearch(tagsp, tagsn, filterByName, pageable, user));
  }
  @GetMapping("/lectors")
  public ResponseEntity<?> lectorSearch(@RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
  @RequestParam(required = false) List<Long> tagsp, @RequestParam(required = false) List<Long> tagsn,
  @RequestParam String filterByName, Pageable pageable) {
    var user = jwtService.getUserFromAuthOk(authToken);
    return ResponseEntity.ok(searchService.lectorSearch(tagsp, tagsn, filterByName, pageable, user));
  }
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_LECTOR')")
  @GetMapping("/event-polling")
  public ResponseEntity<?> eventPolling(
    @RequestHeader(AuthConstants.AUTH_KEY) String authToken, 
    Pageable pageable) {
    var user = jwtService.getUserFromAuth(authToken);
    return ResponseEntity.ok(searchService.eventPolling(pageable, user));
  }

}
