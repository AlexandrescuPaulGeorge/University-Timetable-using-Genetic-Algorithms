package com.licenta.aplicatie.service;

import com.licenta.aplicatie.auth.RoomResponse;
import com.licenta.aplicatie.models.Room;
import com.licenta.aplicatie.repository.DepartmentRepository;
import com.licenta.aplicatie.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, DepartmentRepository departmentRepository) {
        this.roomRepository = roomRepository;
        this.departmentRepository = departmentRepository;
    }

    public Room addRoom(Integer departmentId, Room room) {
        return departmentRepository.findById(departmentId).map(department -> {
            room.setDepartment(department);
            return roomRepository.save(room);
        }).orElseThrow(() -> new RuntimeException("No department with matching id found"));
    }

    public List<RoomResponse> getAllRoomNames() {
        return roomRepository.findAll()
                .stream()
                .map(room -> new RoomResponse(room.getName()))
                .collect(Collectors.toList());
    }
}
