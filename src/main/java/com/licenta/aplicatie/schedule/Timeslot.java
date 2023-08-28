package com.licenta.aplicatie.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.licenta.aplicatie.models.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Entity
@Table(name = "_timeslot")
public class Timeslot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Subject subject;
    @ManyToOne
    private Teacher teacher;
    @ManyToOne
    private Room room;
    @ManyToOne
    private Group group;
    @ManyToOne
    private SubGroup subGroup;
    @ManyToOne
    private StudyYear studyYear;
    @ManyToOne
    @JsonIgnore
    private Timetable timetable;
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;
    @Enumerated(EnumType.STRING)
    private ClassPeriod classPeriod;
    @Enumerated(EnumType.STRING)
    private TeachingStyle teachingStyle;
    private static final Random random = new Random();
    public Timeslot(Timeslot other) {
        this.subject = other.subject;
        this.teacher = other.teacher;
        this.room = other.room;
        this.group = other.group;
        this.subGroup = other.subGroup;
        this.studyYear = other.studyYear;
        this.dayOfWeek = other.dayOfWeek;
        this.classPeriod = other.classPeriod;
        this.teachingStyle = other.teachingStyle;
    }
    public Timeslot copyTimeslot() {
        return new Timeslot(this);
    }
    public Timeslot() {
    }
    public List<Timeslot> generateValidTimeslots(List<Subject> subjects, List<Group> groups, List<Teacher> teachers, List<Room> rooms, List<SubGroup> subGroups, List<StudyYear> studyYears) {
        if (subjects.isEmpty() || groups.isEmpty() || teachers.isEmpty() || rooms.isEmpty() || subGroups.isEmpty() || studyYears.isEmpty()) {
            throw new IllegalArgumentException("No sufficient data found in the database");
        }
        List<Timeslot> timeslots = new ArrayList<>();
        for (StudyYear studyYear : studyYears) {
            List<Subject> subjectsForYear = subjects.stream()
                    .filter(subject -> subject.getStudyYear().equals(studyYear))
                    .collect(Collectors.toList());
            if (subjectsForYear.isEmpty()) {
                throw new IllegalArgumentException("No subjects found for the selected study year");
            }
            for (Subject subject : subjectsForYear) {
                if (subject.getTeachingStyles().contains(TeachingStyle.CURS)) {
                    List<Room> matchingRooms = rooms.stream()
                            .filter(room -> room.getRoomType().name().equals("CURS")) // corrected
                            .collect(Collectors.toList());
                    if (matchingRooms.isEmpty()) {
                        throw new IllegalArgumentException("No rooms found that match the selected teaching style");
                    }
                    Room room = matchingRooms.get(random.nextInt(matchingRooms.size()));
                    List<Teacher> subjectTeachers = new ArrayList<>(subject.getTeachers());
                    Collections.shuffle(subjectTeachers);
                    Teacher teacher = subjectTeachers.get(0);
                    Timeslot timeslot = new Timeslot();
                    timeslot.setSubject(subject);
                    timeslot.setRoom(room);
                    timeslot.setTeacher(teacher);
                    timeslot.setTeachingStyle(TeachingStyle.CURS);
                    timeslot.setDayOfWeek(DayOfWeek.values()[random.nextInt(DayOfWeek.values().length)]);
                    timeslot.setClassPeriod(ClassPeriod.values()[random.nextInt(ClassPeriod.values().length)]);
                    timeslot.setStudyYear(studyYear);
                    timeslots.add(timeslot);
                }
            }
        }
        for (StudyYear studyYear : studyYears) {
            List<Subject> subjectsForYear = subjects.stream()
                    .filter(subject -> subject.getStudyYear().equals(studyYear))
                    .collect(Collectors.toList());
            for (Group group : groups) {
                if (!group.getStudyYear().equals(studyYear)) continue;
                for (Subject subject : subjectsForYear) {
                    for (TeachingStyle teachingStyle : subject.getTeachingStyles()) {
                        if (teachingStyle == TeachingStyle.CURS) continue;
                        List<Room> matchingRooms = rooms.stream()
                                .filter(room -> room.getRoomType().name().equals(teachingStyle.name()))
                                .collect(Collectors.toList());
                        if (matchingRooms.isEmpty()) {
                            throw new IllegalArgumentException("No rooms found that match the selected teaching style");
                        }
                        Room room = matchingRooms.get(random.nextInt(matchingRooms.size()));
                        List<Teacher> subjectTeachers = new ArrayList<>(subject.getTeachers());
                        Collections.shuffle(subjectTeachers);
                        Teacher teacher = subjectTeachers.get(0);
                        Timeslot timeslot = new Timeslot();
                        timeslot.setSubject(subject);
                        timeslot.setRoom(room);
                        timeslot.setTeacher(teacher);
                        timeslot.setTeachingStyle(teachingStyle);
                        timeslot.setDayOfWeek(DayOfWeek.values()[random.nextInt(DayOfWeek.values().length)]);
                        timeslot.setClassPeriod(ClassPeriod.values()[random.nextInt(ClassPeriod.values().length)]);
                        switch (teachingStyle) {
                            case SEMINAR:
                                timeslot.setGroup(group);
                                timeslot.setSubGroup(null);
                                break;
                            case LABORATOR:
                                for (SubGroup subGroup : subGroups) {
                                    if (!subGroup.getGroup().equals(group)) continue;

                                    Room labRoom = matchingRooms.get(random.nextInt(matchingRooms.size()));
                                    Collections.shuffle(subjectTeachers);
                                    Teacher labTeacher = subjectTeachers.get(0);
                                    DayOfWeek labDayOfWeek = DayOfWeek.values()[random.nextInt(DayOfWeek.values().length)];
                                    ClassPeriod labClassPeriod = ClassPeriod.values()[random.nextInt(ClassPeriod.values().length)];

                                    Timeslot labTimeslot = timeslot.copyTimeslot();
                                    labTimeslot.setRoom(labRoom);
                                    labTimeslot.setTeacher(labTeacher);
                                    labTimeslot.setDayOfWeek(labDayOfWeek);
                                    labTimeslot.setClassPeriod(labClassPeriod);
                                    labTimeslot.setGroup(null);
                                    labTimeslot.setSubGroup(subGroup);
                                    timeslots.add(labTimeslot);
                                }
                                continue;
                        }
                        timeslots.add(timeslot);
                    }
                }
            }
        }
        return timeslots;
    }

    public void setStudyYear(StudyYear studyYear) {
        this.studyYear=studyYear;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
    public Teacher getTeacher() {
        return teacher;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
    public Room getRoom() {
        return room;
    }
    public void setGroup(Group group) {
        this.group = group;
    }
    public Group getGroup() {
        return group;
    }
    public void setSubGroup(SubGroup subGroup) {
        this.subGroup = subGroup;
    }

    public SubGroup getSubGroup() {
        return subGroup;
    }

    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
    }

    public Timetable getTimetable() {
        return timetable;
    }
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setClassPeriod(ClassPeriod classPeriod) {
        this.classPeriod = classPeriod;
    }

    public ClassPeriod getClassPeriod() {
        return classPeriod;
    }

    public void setTeachingStyle(TeachingStyle teachingStyle) {
        this.teachingStyle = teachingStyle;
    }
    public TeachingStyle getTeachingStyle() {
        return teachingStyle;
    }

    public StudyYear getStudyYear() {
        return studyYear;
    }
}


