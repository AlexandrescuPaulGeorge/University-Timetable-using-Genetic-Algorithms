package com.licenta.aplicatie.service;

import com.licenta.aplicatie.models.*;
import com.licenta.aplicatie.repository.*;
import com.licenta.aplicatie.schedule.GeneticAlgorithm;
import com.licenta.aplicatie.schedule.Timeslot;
import com.licenta.aplicatie.schedule.Timetable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimetableService {
    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final SubGroupRepository subGroupRepository;
    private final TimetableRepository timetableRepository;

    private List<Teacher> teachers;
    private List<Room> rooms;
    private List<Subject> subjects;
    private List<Group> groups;
    private List<SubGroup> subGroups;

    @Autowired
    public TimetableService(TeacherRepository teacherRepository, RoomRepository roomRepository,
                            SubjectRepository subjectRepository, GroupRepository groupRepository,
                            SubGroupRepository subGroupRepository,
                            TimetableRepository timetableRepository) {
        this.teacherRepository = teacherRepository;
        this.roomRepository = roomRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
        this.subGroupRepository = subGroupRepository;
        this.timetableRepository = timetableRepository;
    }

    public Timetable getTimetableById(Integer id) {
        return timetableRepository.findById(id).orElseThrow(() -> new RuntimeException("Timetable not found"));
    }

    public void loadData() {
        teachers = teacherRepository.findAll();
        rooms = roomRepository.findAll();
        subjects = subjectRepository.findAll();
        groups = groupRepository.findAll();
        subGroups = subGroupRepository.findAll();
    }
}

