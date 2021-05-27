package com.emperium.DAO;

import com.emperium.domain.Measurement;

import java.util.List;

public interface MeasurementDAO {

    boolean measurementsAreSet(int day_id);

    void checkAndUpdateDailyMeasurement(int day_id, List<Measurement> measurement);

    void setDailyMeasurements(List<Measurement> measurements, int day_id);

    void deleteByCityId(int city_id);

}
