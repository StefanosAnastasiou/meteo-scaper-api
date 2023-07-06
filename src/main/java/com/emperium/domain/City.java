package com.emperium.domain;

import java.util.ArrayList;
import java.util.List;

public class City {

    private String name;
    private List<Day> days;

    public City(String name) {
        this.name = name;
        this.days = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
}