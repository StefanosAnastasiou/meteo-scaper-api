package com.emperium.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Day {

    public LocalDate date;
    public List<Measurement> measurements;

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
}