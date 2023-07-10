package com.emperium.scraper;

import com.emperium.DAO.*;
import com.emperium.domainToModelAdapter.Adapter;
import com.emperium.model.City;
import com.emperium.model.Day;
import com.emperium.model.Measurement;
import com.emperium.scheduler.ScrapeScheduler;
import com.emperium.utils.Mappings;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public class MeteoScraper implements Job {

    private Adapter adapter = new Adapter();
    private Logger logger = Logger.getLogger(ScrapeScheduler.class);

    private Day modelDay;
    private Measurement modelMeasurements;

    private CityDAO cityDAO = new CityDAOImpl();
    private DayDAO dayDAO = new DayDAOImpl();
    private MeasurementDAO measurementsDAO = new MeasurementDaoImpl();

    private int city_id;

    private com.emperium.domain.City ct;

    public void init() throws SchedulerException {
        loggerConfig();
        startJob();
    }

    private void initiate() {
        Mappings.cityMappings.keySet()
                .forEach(this::scrapeAndSave);
    }

    private void scrapeAndSave(int ck) {
        try {
            CityScraper cityScraper = new CityScraper(ck);
            cityScraper.scrapeCity();

            ct = cityScraper.getCity();

            if(cityIsSet.test(ct.getName())) {
                city_id = cityDAO.getCityId(ct.getName());

                ct.getDays().forEach(domainDay -> {

                    if(dayIsSet.test(domainDay.getDate(), city_id)) {
                        int day_id = dayDAO.getDayId(domainDay.getDate(), city_id);

                        if (dailyMeasurementsAreSet.test(day_id)) {
                            measurementsDAO.checkAndUpdateDailyMeasurement(day_id, domainDay.getMeasurements());
                        } else {
                            measurementsDAO.setDailyMeasurements(domainDay.getMeasurements(), day_id);
                        }
                    } else {
                        insertRecords(true, domainDay);
                    }
                });
                deletePreviousMeasurements.accept(city_id);
                deletePreviousDays.accept(city_id);
            } else {
                insertRecords(false, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Predicate<String> cityIsSet = city -> cityDAO.cityIsSet(city);

    public BiPredicate<LocalDate, Integer> dayIsSet = (date, city_id) -> dayDAO.dayIsSet(date, city_id);

    public Predicate<Integer> dailyMeasurementsAreSet = day_id -> measurementsDAO.measurementsAreSet(day_id);

    private Consumer<Integer> deletePreviousMeasurements = city_id -> measurementsDAO.deleteByCityId(city_id);

    private Consumer<Integer> deletePreviousDays = city_id -> dayDAO.deleteById(city_id);

    private void insertRecords(boolean cityExists, com.emperium.domain.Day day) {

        if(cityExists) {
            City city = cityDAO.getCityById(this.city_id);

            modelDay = adapter.domainDayToModelAdapter(day, new ArrayList<>());

            day.getMeasurements().forEach(ms -> {
                modelMeasurements = adapter.domainMeasurementsToModelAdapter(ms, new Measurement());

                modelMeasurements.setDay(modelDay);
                modelDay.getMeasurements().add(modelMeasurements);
            });

            dayDAO.insertRecords(modelDay, city);

        } else {
            List<Day> ORMDays = new ArrayList<>();

            ct.getDays().forEach(domainDay -> {
                List<Measurement> ORMMeasurements = new ArrayList<>();

                domainDay.getMeasurements().forEach(m -> {
                    modelMeasurements = adapter.domainMeasurementsToModelAdapter(m, new Measurement());
                    ORMMeasurements.add(modelMeasurements);
                });

                modelDay = adapter.domainDayToModelAdapter(domainDay, ORMMeasurements);
                ORMDays.add(modelDay);

            });
            City modelCity = adapter.domainCityToModelAdapter(ct, ORMDays);

            cityDAO.saveCity(modelCity);
        }
    }

    private void startJob() throws SchedulerException {
        ScrapeScheduler
                .newInstance()
                .getScraperSchedulerFactory()
                .getScraperScheduler()
                .createJob()
                .createTrigger()
                .setScheduler()
                .start();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        logger.debug("Scheduler started successfully");
        try {
            ScrapeScheduler.scheduler
                    .getCurrentlyExecutingJobs()
                    .forEach(job -> {
                        if (job.getJobDetail().getKey().getName().equals(ScrapeScheduler.SCRAPE_CITY_JOB)) {
                            try {
                                initiate();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            logger.error("No such job available");
                        }
                    });
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void loggerConfig() {
        /** Disable HtmlUnit logging */
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }
}