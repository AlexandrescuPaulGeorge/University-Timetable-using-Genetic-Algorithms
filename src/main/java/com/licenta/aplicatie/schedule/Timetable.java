package com.licenta.aplicatie.schedule;

import com.licenta.aplicatie.models.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Entity
@Table(name = "_timetable")
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Timeslot> timeSlots = new ArrayList<>();

    private double fitness = -1;

    public Timetable() {
    }
    public Timetable(List<Timeslot> timeSlots) {
        this.timeSlots = new ArrayList<>(timeSlots);
        for (Timeslot timeslot : timeSlots) {
            timeslot.setTimetable(this);
        }
    }

    public Timetable(Timetable other) {
        this.timeSlots = new ArrayList<>(other.timeSlots);
        this.fitness = other.fitness;
    }
    public void addTimeslot(Timeslot timeslot) {
        this.timeSlots.add(timeslot);
        timeslot.setTimetable(this);
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public List<Timeslot> getTimeSlots() {
        return timeSlots;
    }
    public void setTimeSlots(List<Timeslot> timeSlots) {
        this.timeSlots = timeSlots;
    }
    public double getFitness() {
        return fitness;
    }
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}
