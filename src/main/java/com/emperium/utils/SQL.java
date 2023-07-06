package com.emperium.utils;

import com.emperium.domain.Measurement;
import com.emperium.hibernate.HibernateAnnotationUtil;
import com.emperium.model.City;
import com.emperium.model.Day;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Class that holds all SQL operations.
 */
public class SQL {

    private static SQL instance;

    private SQL(){}

    public static SQL getInstance() {
        if(instance == null){
            instance = new SQL();
        }
        return instance;
    }

    private static final String INSERT_INTO_MEASUREMENT = "INSERT INTO Measurement(time, temperature, humidity, wind, phenomeno, day_id) VALUES" +
            " (:time, :temperature, :humidity, :wind, :phenomeno, :day_id)";

    private final String UPDATE_TEMPERATURE = "UPDATE Measurement SET temperature=:temperature WHERE id=:id";

    private final String UPDATE_HUMIDITY = "UPDATE Measurement SET humidity=:humidity WHERE id=:id";

    private final String UPDATE_WIND = "UPDATE Measurement SET wind=:wind WHERE id=:id";

    private final String UPDATE_PHENOMENO = "UPDATE Measurement SET phenomeno=:phenomeno WHERE id=:id";

    private final String SELECT_CITY_PREDICTIONS_BY_DAY = "SELECT C.city, D.day, M.time, M.temperature, M.wind, M.humidity, M.phenomeno FROM Measurement AS M" +
            " JOIN Day AS D on M.day_id = D.id" +
            " JOIN City C on D.city_id = C.id where D.day=:day AND C.city=:city";

    private final String SELECT_CITY_PREDICTIONS_BY_TIME = "SELECT C.city, D.day, M.time, M.temperature, M.wind, M.humidity, M.phenomeno FROM Measurement AS M" +
            " JOIN Day AS D on M.day_id = D.id" +
            " JOIN City C on D.city_id = C.id where D.day=:day AND C.city=:city AND M.time=:time";

    private final String SELECT_CITY_PREDICTIONS = "SELECT C.city, D.day, M.time, M.temperature, M.wind, M.humidity, M.phenomeno FROM Measurement AS M" +
            " JOIN Day AS D on M.day_id = D.id" +
            " JOIN City AS C on D.city_id = C.id where C.city=:city" +
            " AND D.day >= current_date and M.id >= (SELECT MeasurementId(city))";


    private SessionFactory sessionFactory() {
        SessionFactory sessionFactory = HibernateAnnotationUtil.getSessionFactory();

        return sessionFactory;
    }

    private SessionFactory sessFactory = sessionFactory();

    /**
     * Gets the id of a specified city.
     *
     * @param city
     * @return the id
     */
    public int getCityId(String city) {
        Session session = this.sessFactory.openSession();
        Query query = session.createSQLQuery("SELECT id FROM City WHERE city=:city");
        query.setParameter("city", city);
        List<Object> results = query.getResultList();
        session.close();

        return (int) results.get(0);
    }

    /**
     * Checks if measurements for a specified day are set
     *
     * @param day_id the day id
     * @return true if measurements are set
     */
    public boolean dailyMeasurementsAreSet(int day_id) {
        Session session = this.sessFactory.openSession();
        String hql = "SELECT * FROM Measurement WHERE day_id =:day_id";
        Query query = session.createSQLQuery(hql);
        query.setParameter("day_id", day_id);

        List<Object[]> results = query.getResultList();
        session.close();

        return results.isEmpty() ? false : true;
    }

    /**
     * Gets the id of a specified day that already exists in the database.
     *
     * @param day the day
     * @return the id of the day.
     */
    public int getDayId(LocalDate day, int city_id) {
        String hql = "SELECT id FROM Day WHERE day=:day AND city_id=:city_id";
        Session session = this.sessFactory.openSession();
        Query query = session.createSQLQuery(hql);
        query.setParameter("day", day);
        query.setParameter("city_id", city_id);

        List<Object> result = query.getResultList();
        session.close();

        return (int) result.get(0);
    }

    /**
     * Checks if the date for a specified city is already set.
     *
     * @param day the day
     * @param city_id city id
     * @return true if the date already exists in the database.
     */
    public boolean dayIsSet(LocalDate day, int city_id) {
        Session session = this.sessFactory.openSession();
        String hql = "SELECT day FROM Day WHERE day=:day AND city_id=:city_id";

        Query query = session.createSQLQuery(hql);
        query.setParameter("day", day);
        query.setParameter("city_id", city_id);

        List<Object> result = query.getResultList();
        session.close();

        return !result.isEmpty() ? true : false;
    }

    /**
     * Inserts measurements for a specified day.
     *
     * @param measurements measurements
     * @param dayId day id
     */
    public void setDailyMeasurement(List<Measurement> measurements, int dayId) {
        for(Measurement measurement: measurements) {
            Session session = this.sessFactory.openSession();
            Transaction tx = session.beginTransaction();
            Query query = session.createSQLQuery(INSERT_INTO_MEASUREMENT);
            query.setParameter("time", measurement.getEventTime());
            query.setParameter("temperature", measurement.getTemperature());
            query.setParameter("humidity", measurement.getHumidity());
            query.setParameter("wind", measurement.getWind());
            query.setParameter("phenomeno", measurement.getPhenomeno());
            query.setParameter("day_id", dayId);
            query.executeUpdate();

            commitTransaction(tx);
            session.close();
        }
    }

    /**
     * Checks if measurements are the same and updates id necessary.
     *
     * @param day_id the day id
     * @param measurements list of measurements
     */
    public void checkAndUpdateDailyMeasurement(int day_id, List<Measurement> measurements) {
        Session session = this.sessFactory.openSession();
        String sqlQuery = "SELECT * FROM " +
                "(SELECT * FROM Measurement WHERE day_id = " + day_id + " ORDER BY id DESC LIMIT " +  measurements.size() + ") " +
                "a ORDER BY id;";

        Query query = session.createSQLQuery(sqlQuery);
        List<Object[]> result = query.getResultList();
        session.close();

        int i = 0;
        for(Object[] data : result) {
            if(data[2].equals(measurements.get(i).getTemperature())) {
                assert true;
            } else {
                updateMeasurement(UPDATE_TEMPERATURE, measurements.get(i), data[0], "temperature");
            }

            if(data[3].equals(measurements.get(i).getHumidity())) {
                assert true;
            } else {
                updateMeasurement(UPDATE_HUMIDITY, measurements.get(i), data[0], "humidity");
            }

            if(data[4].equals(measurements.get(i).getWind())) {
                assert true;
            } else {
                updateMeasurement(UPDATE_WIND, measurements.get(i), data[0], "wind");
            }

            if(data[5].equals(measurements.get(i).getPhenomeno())) {
                assert true;
            } else {
                updateMeasurement(UPDATE_PHENOMENO, measurements.get(i), data[0], "phenomeno");
            }
            i++;
        }


        if( i < 8){
            for (int k = i; k <  measurements.size(); k++ ){
                Session sess = this.sessFactory.openSession();
                Transaction tx = sess.beginTransaction();
                Query ms = sess.createSQLQuery(INSERT_INTO_MEASUREMENT);
                ms.setParameter("time", measurements.get(k).getEventTime());
                ms.setParameter("temperature", measurements.get(k).getTemperature());
                ms.setParameter("humidity", measurements.get(k).getHumidity());
                ms.setParameter("wind", measurements.get(k).getWind());
                ms.setParameter("phenomeno", measurements.get(k).getPhenomeno());
                ms.setParameter("day_id", day_id);
                ms.executeUpdate();

                commitTransaction(tx);
                sess.close();
            }
        }
    }

    /**
     * Update any of the measurements specified in the parameter if they differ
     *
     * @param hql the hql
     * @param measurement measurement Object
     * @param id id
     * @param record record
     */
    private void updateMeasurement(String hql, Measurement measurement, Object id, String record) {
        Session session = this.sessFactory.openSession();
        Object val;
        switch(record) {
            case "temperature":
                val = measurement.getTemperature();
                break;
            case "humidity":
                val = measurement.getHumidity();
                break;
            case "wind":
                val = measurement.getWind();
                break;
            default:
                val = measurement.getPhenomeno();
        }

        Transaction tx = session.beginTransaction();
        Query query = session.createSQLQuery(hql);
        query.setParameter(record, val);
        query.setParameter("id", id);
        query.executeUpdate();

        commitTransaction(tx);
        session.close();
    }

    /**
     * Checks if a specified city is already set in the database.
     *
     * @param city city name
     * @return true if a specified city is set
     */
    public boolean cityIsSet(String city) {
        Session session = this.sessFactory.openSession();
        Query query = session.createSQLQuery("SELECT city FROM City WHERE city=:city");
        query.setParameter("city", city);
        List<Object> results = query.getResultList();
        session.close();

        return !results.isEmpty() ? true : false;
    }

    /**
     * Deletes the measurements of the previous days.
     *
     * @param city_id city id used to query and delete previous measurements
     */
     public void deleteMeasurementsByDayId(int city_id) {
        Session session = this.sessFactory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            String hql = "DELETE FROM Measurement WHERE day_id BETWEEN (SELECT id FROM Day WHERE city_id=:city_id AND day < current_date ORDER BY id ASC LIMIT 1)" +
                    " AND (SELECT id FROM Day WHERE city_id=:city_id AND day < current_date ORDER BY id DESC LIMIT 1)";
            Query query = session.createSQLQuery(hql);
            query.setParameter("city_id", city_id);
            query.executeUpdate();
            commitTransaction(tx);
        }catch (Exception e) {
            if(tx != null) tx.rollback();
        }finally {
            session.close();
        }
    }

    /**
     * Deletes days which are earlier than the current day for a specified city
     *
     * @param city_id the city id for which days are deleted
     */
    public void deleteDays(int city_id) {
        Session session = sessFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String sql = "DELETE from Day where city_id=:city_id AND day < current_date";
            Query query = session.createSQLQuery(sql);
            query.setParameter("city_id", city_id);
            query.executeUpdate();
            commitTransaction(tx);
        } catch (Exception e) {
            if(tx != null) tx.rollback();
        } finally {
            session.close();
        }
    }

    /**
     * Saves a city to database.
     *
     * @param city city name
     */
    public void saveCity(City city) {
        Session session = this.sessFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(city);

        commitTransaction(tx);
        session.close();
    }

    /**
     * Inserts new days and measurement into an existing City.
     *
     * @param day the Day entity
     * @param city the City entity
     */
    public void insertByCityId(Day day, City city) {
        Session session = this.sessFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            day.setCity(city);
            session.save(day);
            commitTransaction(tx);
        } catch (Exception e) {
            e.printStackTrace();
            if(tx != null) tx.rollback();
        } finally {
            session.close();
        }

    }

    /**
     * Gets a City entity by id.
     *
     * @param city_id the city id.
     * @return the City entity
     */
    public City getCityById(int city_id) {
        Session session = this.sessFactory.openSession();
        City city = null;
        try{
            session = this.sessFactory.openSession();
            city = session.load(City.class, city_id);
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if(session != null && session.isOpen()) {
                session.close();
            }
        }

        return city;
    }

    /**
     * Gets the predictions for a day for a specified city.
     *
     * @param city city
     * @param date date
     * @return result list
     */
    public List<Object[]> getCityDailyPredictions(String city, String date) {
        Session session = this.sessFactory.openSession();
        Query cityQuery = session.createSQLQuery(SELECT_CITY_PREDICTIONS_BY_DAY);
        cityQuery.setParameter("city", city);
        cityQuery.setParameter("day", date);

        List<Object[]> result = cityQuery.list();
        session.close();

        return result;
    }

    /**
     * Gets the predictions for a specified time of a day, for a specified city.
     *
     * @param city city
     * @param date date
     * @param time time
     * @return result list
     */
    public List<Object[]> getCityPredictionsPerHour(String city, String date, String time) {
        Session session = this.sessFactory.openSession();
        Query query = session.createSQLQuery(SELECT_CITY_PREDICTIONS_BY_TIME);
        query.setParameter("city", city);
        query.setParameter("day", date);
        query.setParameter("time", time);

        List<Object[]> result = query.list();
        session.close();

        return result;
    }

    /**
     * Gets all the predictions for a specified city, from the time requested and onwards.
     *
     * @param city city
     * @return result list
     */
    public List<Object[]> getCityPredictions(String city) {
        Session session = this.sessFactory.openSession();
        Query query = session.createSQLQuery(SELECT_CITY_PREDICTIONS);
        query.setParameter("city", city);

        List<Object[]> result = query.list();
        session.close();

        return result;
    }

    /**
     * Commits a transaction.
     *
     * @param tx transaction object
     */
    private static void commitTransaction(Transaction tx) {
        if(tx.getStatus().equals(TransactionStatus.ACTIVE)) {
            tx.commit();
        }
    }
}