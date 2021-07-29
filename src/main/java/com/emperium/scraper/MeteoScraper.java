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
import java.util.logging.Level;

public class MeteoScraper implements Job {

    private Adapter adapter = new Adapter();
    private CityScraper cityScraper;
    private Logger logger = Logger.getLogger(ScrapeScheduler.class);

    private City modelCity;
    private Day modelDay;
    private Measurement modelMeasurements;

    private CityDAO cityDAO = new CityDAOImpl();
    private DayDAO dayDAO = new DayDAOImpl();
    private MeasurementDAO measurementsDAO = new MeasurementDaoImp();

    private List<Day> ORMDays;
    private List<Measurement> ORMMeasurements;

    private int city_id;

    List<Integer> domainDays = new ArrayList<>();

    private com.emperium.domain.City ct;

    public void init() throws SchedulerException {
        loggerConfig();
        startJob();
    }

    public void initiate() {
        Mappings.cityMappings.keySet()
                .stream()
                .forEach(ck ->initScrapingAndSave(ck));
    }

    public void initScrapingAndSave(int ck) {
                    try {
                        cityScraper = new CityScraper(ck);
                        cityScraper.scrapeCity();

                        this.ct = cityScraper.getCity();


                        if(cityIsSet(this.ct.getName())) {
//                        if(cityDAO.cityIsSet(this.ct.getName())) {
                            this.ORMDays = new ArrayList<>();
                            this.city_id = cityDAO.getCityId(this.ct.getName());

                            ct.days.stream().forEach(domainDay -> {
                                         this.ORMMeasurements = new ArrayList<>();

                                if(dayIsSet(domainDay.getDate(), this.city_id)) {
//                                         if(dayDAO.dayIsSet(domainDay.getDate(), this.city_id)) {
                                             int day_id = dayDAO.getDayId(domainDay.getDate(), this.city_id);

                                         if (dailyMeasurementsAreSet(day_id)) {
                                             //if (measurementsDAO.measurementsAreSet(day_id)) {
                                                 measurementsDAO.checkAndUpdateDailyMeasurement(day_id, domainDay.measurements);
                                             } else {
                                                 measurementsDAO.setDailyMeasurements(domainDay.measurements, day_id);
                                             }
                                             fillDays(day_id);
                                         } else {
                                              insertRecords(true, domainDay);
                                         }
                            });
                            deletePreviousMeasurements(this.city_id);
                            deletePreviousDays(this.city_id);
                            this.domainDays.clear();
                        } else {
                             insertRecords(false, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
    }

    public boolean cityIsSet(String city) {
        return cityDAO.cityIsSet(city);
    }

    public boolean dayIsSet(LocalDate date, int city_id) {
        return dayDAO.dayIsSet(date, city_id);
    }

    public boolean dailyMeasurementsAreSet(int day_id) {
        return measurementsDAO.measurementsAreSet(day_id);
    }

    private void fillDays(int day_id) {
        this.domainDays.add(day_id);
    }

    private void deletePreviousMeasurements(int city_id) {
        measurementsDAO.deleteByCityId(city_id);
    }

    private void deletePreviousDays( int city_id) {
        dayDAO.deleteById(city_id);
    }

    private void insertRecords(boolean cityExists, com.emperium.domain.Day day) {

        if(cityExists) {
            this.ORMDays = new ArrayList<>();
            City city = cityDAO.getCityById(this.city_id);

            modelDay = adapter.domainDayToModelAdapter(day, new Day(), new ArrayList<Measurement>());

            this.ORMMeasurements = new ArrayList<>();

            day.measurements.stream().forEach(ms -> {
                modelMeasurements = adapter.domainMeasurementsToModelAdapter(ms, new Measurement());
                this.ORMMeasurements.add(modelMeasurements);

                modelMeasurements.setDay(modelDay);
                modelDay.getMeasurements().add(modelMeasurements);
            });

            dayDAO.insertRecords(modelDay, city);

        } else {
            this.ORMDays = new ArrayList<>();
            this.ct.days.stream().forEach(domainDay -> {
                this.ORMMeasurements = new ArrayList<>();

                domainDay.measurements.stream().forEach(m -> {
                    modelMeasurements = adapter.domainMeasurementsToModelAdapter(m, new Measurement());
                    this.ORMMeasurements.add(modelMeasurements);
                });

                modelDay = adapter.domainDayToModelAdapter(domainDay, new Day(), this.ORMMeasurements);
                this.ORMDays.add(modelDay);

            });
            modelCity  = adapter.domainCityToModelAdapter(this.ct, new City(), this.ORMDays);

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
                    .stream()
                    .forEach(job -> {
                        if (job.getJobDetail().getKey().getName().equals(ScrapeScheduler.SCRAPE_CITY_JOB)) {
                            try {
                                init();
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