package com.servustech.eduson.utils.taskscheduler;
// from https://www.baeldung.com/spring-task-scheduler

import com.servustech.eduson.features.products.webinars.WebinarService;
import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.features.products.webinars.RunnableWebinar;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import com.servustech.eduson.features.products.webinars.WebinarService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ThreadPoolTaskSchedulerExamples {
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private CronTrigger cronTrigger;

    @Autowired
    private PeriodicTrigger periodicTrigger;

    private final WebinarService webinarService;

    @PostConstruct
    public void scheduleRunnableWithCronTrigger() {
      List<Webinar> webinars = webinarService.getWebinarsToSchedule();
      webinars.stream().forEach(w -> taskScheduler.schedule(new RunnableWebinar(
        w.getName(), w.getStartTime(), w.getId(), webinarService
      ), new Date(w.getStartTime().toInstant().toEpochMilli())));
      webinarService.setTaskScheduler(taskScheduler);
      // taskScheduler.schedule(new RunnableTask("Current Date"), new Date());
      // taskScheduler.schedule(new RunnableTask("Current Date"), new Date(new Date().getTime() + 15 * 1000));
      // taskScheduler.scheduleWithFixedDelay(new RunnableTask("Fixed 1 second Delay"), 1000);
      // taskScheduler.scheduleWithFixedDelay(new RunnableTask("Current Date Fixed 1 second Delay"), new Date(), 1000);
      // taskScheduler.scheduleAtFixedRate(new RunnableTask("Fixed Rate of 2 seconds"), new Date(), 2000);
      // taskScheduler.scheduleAtFixedRate(new RunnableTask("Fixed Rate of 2 seconds"), 2000);
      // taskScheduler.schedule(new RunnableTask("Cron Trigger"), cronTrigger);
      // taskScheduler.schedule(new RunnableTask("Periodic Trigger"), periodicTrigger);
    }

}