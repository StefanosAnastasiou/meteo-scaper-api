package com.emperium.DAO;

import com.emperium.model.City;
import com.emperium.model.Day;
import com.emperium.utils.SQL;

import java.time.LocalDate;

public class DayDAOImpl implements DayDAO {

    @Override
    public boolean dayIsSet(LocalDate date, int city_id) {
        return SQL.getInstance().dayIsSet(date, city_id);
    }

    @Override
    public int getDayId(LocalDate date, int city_id) {
        return SQL.getInstance().getDayId(date, city_id);
    }

    @Override
    public void deleteById(int city_id) {
        SQL.getInstance().deleteDays(city_id);
    }

    @Override
    public void insertRecords(Day day, City city) {
        SQL.getInstance().insertByCityId(day, city);
    }
}
