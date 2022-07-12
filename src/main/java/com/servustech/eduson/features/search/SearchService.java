package com.servustech.eduson.features.search;

import lombok.AllArgsConstructor;

import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.lectors.LectorService;
import com.servustech.eduson.features.products.courses.CourseService;
import com.servustech.eduson.features.products.webinars.WebinarService;
import com.servustech.eduson.features.products.liveEvents.LiveEventService;
import com.servustech.eduson.features.categories.tags.TagService;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SearchService {
  private final LectorService lectorService;
  private final CourseService courseService;
  private final WebinarService webinarService;
  private final LiveEventService liveEventService;
  private final TagService tagService;
  // private final ModuleRepository moduleRepository;

  public SearchResult publicSearch(List<Long> tagsp, List<Long> tagsn, String filterByName, Pageable pageable, User user) {
    var courses = courseService.searchCourses(tagsp, tagsn, filterByName, pageable, user);
    var webinars = webinarService.searchWebinars(tagsp, tagsn, filterByName, pageable, user);
    var events = liveEventService.searchLiveEvents(tagsp, tagsn, filterByName, pageable, user);
    var lectors = lectorService.searchLectors(filterByName, pageable);
    return SearchResult.builder()
        .courses(courses)
        .webinars(webinars)
        .events(events)
        .lectors(lectors)
        .build();
  }
  public SearchResult courseSearch(List<Long> tagsp, List<Long> tagsn, String filterByName, Pageable pageable, User user) {
    var courses = courseService.searchCourses(tagsp, tagsn, filterByName, pageable, user);
    return SearchResult.builder()
        .courses(courses)
        .build();
  }
  public SearchResult webinarSearch(List<Long> tagsp, List<Long> tagsn, String filterByName, Pageable pageable, User user) {
    var webinars = webinarService.searchWebinars(tagsp, tagsn, filterByName, pageable, user);
    return SearchResult.builder()
        .webinars(webinars)
        .build();
  }
  public SearchResult liveEventSearch(List<Long> tagsp, List<Long> tagsn, String filterByName, Pageable pageable, User user) {
    var events = liveEventService.searchLiveEvents(tagsp, tagsn, filterByName, pageable, user);
    return SearchResult.builder()
        .events(events)
        .build();
  }
  public SearchResult lectorSearch(List<Long> tagsp, List<Long> tagsn, String filterByName, Pageable pageable, User user) {
    var lectors = lectorService.searchLectors(filterByName, pageable); // TODO published for lectors
    return SearchResult.builder()
        .lectors(lectors)
        .build();
  }
  public SearchResult getRelated(List<Long> tagsp, List<Long> tagsn, List<Long> coursesn, List<Long> webinarsn, List<Long> eventsn, 
  Pageable pageable) {
    var courses = tagService.getCourses(tagsp, tagsn, coursesn, "", pageable);
    var webinars = tagService.getWebinars(tagsp, tagsn, webinarsn, "", pageable);
    var events = tagService.getLiveEvents(tagsp, tagsn, eventsn, "", pageable);
    return SearchResult.builder()
        .courses(courses)
        .webinars(webinars)
        .events(events)
        .build();
  }
  public SearchResult getRelatedCourses(List<Long> tagsp, List<Long> tagsn, List<Long> coursesn, Pageable pageable) {
    var courses = tagService.getCourses(tagsp, tagsn, coursesn, "", pageable);
    return SearchResult.builder()
        .courses(courses)
        .build();
  }
  public SearchResult getRelatedWebinars(List<Long> tagsp, List<Long> tagsn, List<Long> webinarsn, Pageable pageable) {
    var webinars = tagService.getWebinars(tagsp, tagsn, webinarsn, "", pageable);
    return SearchResult.builder()
        .webinars(webinars)
        .build();
  }
  public SearchResult getRelatedLiveEvents(List<Long> tagsp, List<Long> tagsn, List<Long> eventsn, Pageable pageable) {
    var events = tagService.getLiveEvents(tagsp, tagsn, eventsn, "", pageable);
    return SearchResult.builder()
        .events(events)
        .build();
  }
  public SearchResult eventPolling(Pageable pageable, User user) {
    boolean pageableHasValue = true;
    if (pageable == null) {
      System.out.println("pageable is null");
      pageableHasValue = false;
    }
    if (pageable == Pageable.unpaged()) {
      System.out.println("pageable is unpaged");
      pageableHasValue = false;
    }
    pageable = Pageable.unpaged();

//    var webinars = webinarService.getMyWebinars(user, null, pageable);
//    var events = liveEventService.getMyLiveEvents(user, null, pageable);

    var webinars = webinarService.getMyUpcomingWebinars(pageable, "", user);
    var events = liveEventService.getMyUpcomingLiveEvents(pageable, "", user);

    return SearchResult.builder()
      .events(events)
      .webinars(webinars)
    .build();
  }

}
