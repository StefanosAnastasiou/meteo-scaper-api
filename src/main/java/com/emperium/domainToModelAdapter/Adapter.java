package com.emperium.domainToModelAdapter;

import com.emperium.domain.City;
import com.emperium.domain.Day;
import com.emperium.domain.Measurement;

import java.util.List;

/**
 * Maps scraped measurements to model
 */
public class Adapter {

    public com.emperium.model.City domainCityToModelAdapter(City domainCity, List<com.emperium.model.Day> days) {
        com.emperium.model.City modelCity = new com.emperium.model.City();
        modelCity.setName(domainCity.getName());
        modelCity.setDays(days);

        return modelCity;
    }

    public com.emperium.model.Day domainDayToModelAdapter(Day domainDay, List<com.emperium.model.Measurement> measurements) {
        com.emperium.model.Day modelDay = new com.emperium.model.Day();
        modelDay.setDay(domainDay.getDate());
        modelDay.setMeasurements(measurements);

        return modelDay;
    }

    public com.emperium.model.Measurement domainMeasurementsToModelAdapter(Measurement domainMeasure, com.emperium.model.Measurement modelMeasure) {
        modelMeasure.setTemperature(domainMeasure.getTemperature());
        modelMeasure.setHumidity(domainMeasure.getHumidity());
        modelMeasure.setWind(domainMeasure.getWind());
        modelMeasure.setTime(domainMeasure.getEventTime());
        modelMeasure.setPhenomeno(domainMeasure.getPhenomeno());

        return modelMeasure;
    }
}
