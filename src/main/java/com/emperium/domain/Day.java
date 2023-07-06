package com.emperium.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Day {

    private LocalDate date;
    private List<Measurement> measurements;

    public Day(LocalDate date) {
        this.date = date;
        this.measurements = new ArrayList<>();
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date){
        this.date = date;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
}