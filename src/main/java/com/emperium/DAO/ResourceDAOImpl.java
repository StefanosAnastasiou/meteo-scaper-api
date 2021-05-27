package com.emperium.DAO;

import com.emperium.utils.SQL;

import java.util.List;

public class ResourceDAOImpl implements ResourceDAO{

    @Override
    public List<Object[]> getCityDailyPredictions(String city, String date) {
        return SQL.getInstance().getCityDailyPredictions(city, date);
    }

    @Override
    public List<Object[]> getCityPredictionsPerHour(String city, String date, String time) {
        return SQL.getInstance().getCityPredictionsPerHour(city, date, time);
    }

    @Override
    public List<Object[]> getCityPredictions(String city) {
        return SQL.getInstance().getCityPredictions(city);
    }
}
