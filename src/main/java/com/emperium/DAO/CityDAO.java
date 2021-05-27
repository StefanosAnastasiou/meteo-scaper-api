package com.emperium.DAO;

import com.emperium.model.City;

public interface CityDAO {

    boolean cityIsSet(String city);

    int getCityId(String city);

    void saveCity(City city);

    City getCityById(int id);
}
