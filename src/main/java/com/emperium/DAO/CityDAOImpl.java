package com.emperium.DAO;

import com.emperium.model.City;
import com.emperium.utils.SQL;

public class CityDAOImpl implements CityDAO {

    @Override
    public void saveCity(City city) {
        SQL.getInstance().saveCity(city);
    }

    @Override
    public boolean cityIsSet(String city) {
        return SQL.getInstance().cityIsSet(city);
    }

    @Override
    public int getCityId(String city) {
        return SQL.getInstance().getCityId(city);
    }

    @Override
    public City getCityById(int id) {
        return SQL.getInstance().getCityById(id);
    }
}