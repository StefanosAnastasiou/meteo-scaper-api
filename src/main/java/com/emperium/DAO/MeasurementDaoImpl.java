package com.emperium.DAO;

import com.emperium.domain.Measurement;
import com.emperium.utils.SQL;

import java.util.List;


public class MeasurementDaoImpl implements MeasurementDAO {

    @Override
    public boolean measurementsAreSet(int day_id) {
            return SQL.getInstance().dailyMeasurementsAreSet(day_id);
    }

    @Override
    public void checkAndUpdateDailyMeasurement(int day_id, List<Measurement> measurements) {
             SQL.getInstance().checkAndUpdateDailyMeasurement(day_id, measurements);
    }

    @Override
    public void setDailyMeasurements(List<Measurement> measurements, int day_id) {
        SQL.getInstance().setDailyMeasurement(measurements, day_id);
    }

    @Override
    public void deleteByCityId(int city_id) { SQL.getInstance().deleteMeasurementsByDayId(city_id); }

}