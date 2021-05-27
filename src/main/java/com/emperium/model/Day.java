package com.emperium.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity(name = "Day")
@Table(name = "Day", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class Day implements Serializable {

    private static final long serialVersionUID = -5134756472180975391L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "day", unique = true, nullable = false, length = 100)
    private LocalDate day;

    @JoinColumn(name = "city_id", referencedColumnName = "id")
    @ManyToOne
    private City city;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id")
    @OrderBy("time")
    List<Measurement> measurements;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
}
