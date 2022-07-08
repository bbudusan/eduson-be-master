package com.servustech.eduson.features.products.webinars;

import java.time.ZonedDateTime;
import java.lang.ProcessBuilder;
import java.io.IOException;


public class RunnableWebinar implements Runnable {

  private String name;
  private ZonedDateTime startTime;
  private Long webinarId;
  private WebinarService webinarService;

  public Long getId() {
    return webinarId;
  }

  public RunnableWebinar(String name, ZonedDateTime startTime, Long webinarId, WebinarService webinarService) {
      this.name = name;
      this.startTime = startTime;
      this.webinarId = webinarId;
      this.webinarService = webinarService;
  }

  @Override
  public void run() {
      System.out.println("Webinar " + name + " scheduled live at " + startTime + " on thread " + Thread.currentThread().getName());
      Webinar webinar = webinarService.findById(webinarId);
      if (webinar.getStartTime().toInstant().toEpochMilli() != startTime.toInstant().toEpochMilli()) {
        // try https://stackoverflow.com/questions/14889143/how-to-stop-a-task-in-scheduledthreadpoolexecutor-once-i-think-its-completed if this aproach does not work
        System.out.println("Webinar " + name + " has been unscheduled from " + startTime + ". It's starting time is on " + webinar.getStartTime());
        return; // do not start, as startTime was modified. TODO reschedule then at modifying the startTime.
      }
      // webinarService.stream(webinarId, true); // org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.servustech.eduson.features.products.courses.Course.adverts, could not initialize proxy - no Session
      try {
        ProcessBuilder builder = new ProcessBuilder(
          "curl",
          "-X", "PUT", 
          "http://localhost:8080/api/webinar/livehdsfbjdsfsbfsahfsavf/"+webinarId.toString()
        );
        builder.inheritIO();
        Process process = builder.start();
        int exitCode = process.waitFor();
        System.out.println("done schedule live of webinar "+webinarId+" " + exitCode);
      } catch (java.io.IOException e) {
        System.out.println("schedule io error"); // TODO
        System.out.println(e.toString());
        return;
      } catch (java.lang.InterruptedException e) {
        System.out.println("schedule interrupted error"); // TODO
        System.out.println(e.toString());
        return;
      }

  }
}
