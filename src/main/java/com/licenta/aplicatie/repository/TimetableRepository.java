package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.schedule.Timeslot;
import com.licenta.aplicatie.schedule.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableRepository extends JpaRepository<Timetable, Integer> {
}
