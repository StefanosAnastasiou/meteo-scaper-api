package com.emperium.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;

@Entity(name = "Measurement")
@Table(name = "Measurement", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class Measurement implements Serializable {

    public static final long serialVersionUID = 7801230791440883698L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "temperature")
    private int temperature;

    @Column(name = "humidity")
    private int humidity;

    @Column(name = "wind")
    private String wind;

    @Column(name = "phenomeno")
    private String phenomeno;

    @JoinColumn(name = "day_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Day day;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getPhenomeno() {
        return phenomeno;
    }

    public void setPhenomeno(String phenomeno) {
        this.phenomeno = phenomeno;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }
}