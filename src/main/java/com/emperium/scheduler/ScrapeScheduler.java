package com.emperium.scheduler;

import com.emperium.scraper.MeteoScraper;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Wrapper class for Quartz Scheduler
 *
 */
public class ScrapeScheduler {

    private SchedulerFactory factory;
    public static Scheduler scheduler;
    private JobDetail jobDetail;
    private Trigger trigger;

    public static final String SCRAPE_CITY_JOB = "scrapeJob";

    public static ScrapeScheduler newInstance() {
        return new ScrapeScheduler();
    }

    public ScrapeScheduler getScraperSchedulerFactory() {
        factory = new StdSchedulerFactory();
        return this;
    }

    public ScrapeScheduler getScraperScheduler() throws SchedulerException {
        scheduler = factory.getScheduler();
        return this;
    }

    public ScrapeScheduler createJob() {
        jobDetail = JobBuilder.newJob(MeteoScraper.class)
                .withIdentity(SCRAPE_CITY_JOB)
                .build();
        return this;
    }

    public ScrapeScheduler createTrigger() {
        trigger = newTrigger()
                .withIdentity("CronTrigger")
//    Uncomment these line for testing and comment the cron bellow.
//                .startNow()
//                .withSchedule(simpleSchedule()
//                              .withIntervalInSeconds(480)
//                             .repeatForever())
//                .build();
                .withSchedule(cronSchedule("0 55 23 * * ?"))   // Cron fires every night at 23:55
                .forJob("scrapeJob")
                .build();

        return this;
    }

    public ScrapeScheduler setScheduler() throws SchedulerException {
        scheduler.scheduleJob(jobDetail, trigger);
        return this;
    }

    public ScrapeScheduler start() throws SchedulerException {
        scheduler.start();
        return this;
    }
}