package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.auth.RoomRequest;
import com.licenta.aplicatie.auth.RoomResponse;
import com.licenta.aplicatie.models.Room;
import com.licenta.aplicatie.service.DepartmentService;
import com.licenta.aplicatie.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments/{departmentId}/rooms")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> addRoom(@PathVariable Integer departmentId, @RequestBody RoomRequest roomRequest) {
        Room room = new Room();
        room.setName(roomRequest.getName());
        room.setExtern(roomRequest.getExtern());
        room.setRoomType(roomRequest.getRoomType());

        Room addedRoom = roomService.addRoom(departmentId, room);
        return new ResponseEntity<>(addedRoom, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<RoomResponse>> getAllRoomNames() {
        List<RoomResponse> roomNames = roomService.getAllRoomNames();
        return ResponseEntity.ok(roomNames);
    }
}

