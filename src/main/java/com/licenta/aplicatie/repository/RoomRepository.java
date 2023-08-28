package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.models.Room;
import com.licenta.aplicatie.models.StudyYear;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {

}