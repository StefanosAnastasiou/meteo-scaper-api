package com.emperium.DAO;

import java.util.List;

public interface ResourceDAO {
    List<Object[]> getCityDailyPredictions(String city, String date);

    List<Object[]> getCityPredictionsPerHour(String city, String date, String time);

    List<Object[]> getCityPredictions(String city);
}
