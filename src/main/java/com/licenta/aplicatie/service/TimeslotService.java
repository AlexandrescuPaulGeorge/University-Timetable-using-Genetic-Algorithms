package com.licenta.aplicatie.service;

import com.licenta.aplicatie.models.*;
import com.licenta.aplicatie.repository.*;
import com.licenta.aplicatie.schedule.Timeslot;
import com.licenta.aplicatie.schedule.Timetable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeslotService {
    private final TimeslotRepository timeslotRepository;
    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final SubGroupRepository subGroupRepository;
    private final StudyYearRepository studyYearRepository;
    @Autowired
    private final TimetableRepository timetableRepository;

    @Autowired
    public TimeslotService(TimeslotRepository timeslotRepository, TeacherRepository teacherRepository, RoomRepository roomRepository,
                           SubjectRepository subjectRepository, GroupRepository groupRepository,
                           SubGroupRepository subGroupRepository,StudyYearRepository studyYearRepository,TimetableRepository timetableRepository) {
        this.timeslotRepository = timeslotRepository;
        this.teacherRepository = teacherRepository;
        this.roomRepository = roomRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
        this.subGroupRepository = subGroupRepository;
        this.studyYearRepository = studyYearRepository;
        this.timetableRepository=timetableRepository;
    }
    public Timetable generateRandomTimeslots() {
        List<Subject> subjects = subjectRepository.findAll();
        List<Group> groups = groupRepository.findAll();
        List<Teacher> teachers = teacherRepository.findAll();
        List<Room> rooms = roomRepository.findAll();
        List<SubGroup> subGroups = subGroupRepository.findAll();
        List<StudyYear> studyYears = studyYearRepository.findAll();
        // Fetch StudyYear instances
        Timetable timetable = new Timetable();
        timetable = timetableRepository.save(timetable);
        Timeslot timeslot = new Timeslot();

        List<Timeslot> timeslots = timeslot.generateValidTimeslots(subjects, groups, teachers, rooms, subGroups, studyYears);  // Pass StudyYear instances
        for (Timeslot generatedTimeslot : timeslots) {
            generatedTimeslot.setTimetable(timetable);
            timetable.addTimeslot(generatedTimeslot);
        }
        timeslotRepository.saveAll(timeslots);
        //timetableRepository.save(timetable); // Update timetable with timeslots

        return timetable;
    }
}


