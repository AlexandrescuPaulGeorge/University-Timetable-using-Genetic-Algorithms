package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.models.University;
import com.licenta.aplicatie.schedule.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeslotRepository extends JpaRepository<Timeslot, Integer> {
}
