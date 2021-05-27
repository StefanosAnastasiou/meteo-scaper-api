package com.emperium.domainToModelAdapter;

import com.emperium.domain.City;
import com.emperium.domain.Day;
import com.emperium.domain.Measurement;

import java.util.List;

/**
 * Maps scraped measurements to model
 */
public class Adapter {

    public com.emperium.model.City domainCityToModelAdapter(City domainCity, com.emperium.model.City modelCity, List<com.emperium.model.Day> days) {
        modelCity.setName(domainCity.getName());
        modelCity.setDays(days);

        return modelCity;
    }

    public com.emperium.model.Day domainDayToModelAdapter(Day domainDay, com.emperium.model.Day modelDay, List<com.emperium.model.Measurement> measurements) {
        modelDay.setDay(domainDay.date);
        modelDay.setMeasurements(measurements);

        return modelDay;
    }

    public com.emperium.model.Measurement domainMeasurementsToModelAdapter(Measurement domainMeasure, com.emperium.model.Measurement modelMeasure) {
        modelMeasure.setTemperature(domainMeasure.temperature);
        modelMeasure.setHumidity(domainMeasure.humidity);
        modelMeasure.setWind(domainMeasure.wind);
        modelMeasure.setTime(domainMeasure.eventTime);
        modelMeasure.setPhenomeno(domainMeasure.phenomeno);

        return modelMeasure;
    }
}
