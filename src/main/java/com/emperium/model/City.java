package com.emperium.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity(name = "City")
@Table(name = "City", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class City implements Serializable {

    private static final long serialVersionUID = 2753237981101044893L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, length = 100)
    private int id;

    @Column(name = "city", unique = true, nullable = false, length = 100)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "city_id")
    List<Day> days;

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
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
