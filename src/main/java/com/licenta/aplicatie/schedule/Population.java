package com.licenta.aplicatie.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Population {
    private List<Timetable> timetables;

    public Population() {
        this.timetables = new ArrayList<>();
    }
    public void addTimetable(Timetable timetable) {
        this.timetables.add(timetable);
    }

    public Timetable getBestTimetable() {
        return Collections.max(timetables, Comparator.comparing(Timetable::getFitness));
    }
    public List<Timetable> getTimetables() {
        return this.timetables;
    }
    public void setTimetables(List<Timetable> newTimetables) {
        this.timetables = newTimetables;
    }
}
