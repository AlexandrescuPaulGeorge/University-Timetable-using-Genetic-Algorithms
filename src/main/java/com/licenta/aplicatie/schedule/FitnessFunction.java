package com.licenta.aplicatie.schedule;

import com.licenta.aplicatie.models.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FitnessFunction {

    //Conflicte "basic"
    private static final double TIMESLOT_CONFLICT_PENALTY = 1.0;
    private static final double TEACHER_TIME_CONFLICT_PENALTY = 1.0;
    private static final double GROUP_TIME_CONFLICT_PENALTY = 1.0;
    private static final double SUBGROUP_TIME_CONFLICT_PENALTY = 1.0;

    private static final double STUDY_YEAR_TIME_CONFLICT_PENALTY = 1.0;

    private static final double ROOM_TIME_CONFLICT_PENALTY = 1.0;

    private static final double SUBGROUP_SUBJECT_TEACHING_STYLE_CONFLICT_PENALTY = 1.0;

    private static final double SAME_SUBJECT_TEACHING_STYLE_TIME_CONFLICT_PENALTY = 1.0;


    //Conflicte speciale
    private static final double ROOM_DAY_CONFLICT_PENALTY = 0.0; //Conflict sala X nu poate fi folosita in ziua de (INSERT_HERE)
    private static final double TEACHER_DAY_TIME_CONFLICT_PENALTY = 0.0; //CONFLICT PROFESOR(HERGHELEGIU) LUNI DIMINEATA(8.00-14.00)
    private static final double TEACHING_ORDER_PENALTY1 = 0.0; //Respectata ordinea CURS->SEMINAR(in functie de subject)
    private static final double TEACHING_ORDER_PENALTY2 = 0.0; //Respectata ordinea CURS->LABORATOR(in functie de subject)
    private static final double CURS_LAB_CURS_CONFLICT_PENALTY = 0.0;  // Nu putem ave 3 timesloturi consecutive cu CURS->LABORATOR->LABORATOR
    private static final double CONSECUTIVE_DAY_PENALTY = 0.0;  // Daca avem un ..!!!!
    private static final double FRIDAY_AFTERNOON_CLASSES_PENALTY = 2.0; //Nu putem avea CURSURI seare de la 18.00
    private static final double LATE_CLASS_PENALTY = 0.0;

    //Fitnessul maxim din care vom scadea puncte in functie de conflictele incalcate
    private static final double MAX_FITNESS = 1000.0;

    public double calculateFitness(Timetable timetable) {
        double fitness = MAX_FITNESS;
        System.out.println("(FitnessFunction)Calculating fitness for timetable: " + timetable.getId());
        fitness -= calculateTeacherTimeConflictPenalty(timetable);
         fitness -= calculateGroupTimeConflictPenalty(timetable);
        fitness -= calculateSubGroupTimeConflictPenalty(timetable);
         fitness -= calculateRoomTimeConflictPenalty(timetable);
        fitness -= calculateTeachingOrderPenalty1(timetable);
        fitness -= calculateTeachingOrderPenalty2(timetable);
         fitness -= calculateStudyYearTimeConflictPenalty(timetable);
         fitness -= calculateRoomDayViolations(timetable);
        fitness -= calculateTeacherDayViolations(timetable);
        fitness -= calculateSubGroupSubjectTeachingStyleConflictPenalty(timetable);
        fitness -= calculateSameSubjectTeachingStyleTimeConflictPenalty(timetable);
        fitness -= calculateTimeslotConflictPenalty(timetable);
        fitness -= calculateCursLabCursConflictPenalty(timetable);
        fitness -= calculateConsecutiveDayPenalty(timetable);
        fitness -= calculateNoFridayAfternoonClassesPenalty(timetable);
        fitness -= calculateLateClassPenalty(timetable);
        System.out.println("(FitnessFunction)Fitness calculated: " + fitness);
        return fitness;
    }

    public double calculateLateClassPenalty(Timetable timetable) {
        return LATE_CLASS_PENALTY * countLateClassPeriod(timetable);
    }

    public double calculateNoFridayAfternoonClassesPenalty(Timetable timetable) {
        return FRIDAY_AFTERNOON_CLASSES_PENALTY * countFridayAfternoonClasses(timetable);
    }

    public double calculateConsecutiveDayPenalty(Timetable timetable) {
        return CONSECUTIVE_DAY_PENALTY * countConsecutiveDayConflicts(timetable);
    }
    public double calculateTimeslotConflictPenalty(Timetable timetable) {
        return TIMESLOT_CONFLICT_PENALTY * countTimeslotConflicts(timetable);
    }
    public double calculateCursLabCursConflictPenalty(Timetable timetable) {
        return CURS_LAB_CURS_CONFLICT_PENALTY * countCursLabCursConflicts(timetable);
    }

    public double calculateSameSubjectTeachingStyleTimeConflictPenalty(Timetable timetable) {
        return countSameSubjectTeachingStyleTimeConflicts(timetable) * SAME_SUBJECT_TEACHING_STYLE_TIME_CONFLICT_PENALTY;
    }
    public double calculateRoomDayViolations(Timetable timetable){
        return ROOM_DAY_CONFLICT_PENALTY * countRoomDayViolations(timetable);
    }

    public double calculateTeacherTimeConflictPenalty(Timetable timetable) {
        return TEACHER_TIME_CONFLICT_PENALTY * countTeacherTimeConflict(timetable);
    }

    public double calculateStudyYearTimeConflictPenalty(Timetable timetable) {
        return countStudyYearTimeConflict(timetable) * STUDY_YEAR_TIME_CONFLICT_PENALTY;
    }

    public double calculateGroupTimeConflictPenalty(Timetable timetable) {
        return countGroupTimeConflict(timetable) * GROUP_TIME_CONFLICT_PENALTY;
    }

    public double calculateSubGroupTimeConflictPenalty(Timetable timetable) {
        return countSubGroupTimeConflict(timetable) * SUBGROUP_TIME_CONFLICT_PENALTY;
    }

    public double calculateTeachingOrderPenalty1(Timetable timetable) {
        return countTeachingOrderViolations1(timetable) * TEACHING_ORDER_PENALTY1;
    }

    public double calculateTeachingOrderPenalty2(Timetable timetable) {
        return countTeachingOrderViolations2(timetable) * TEACHING_ORDER_PENALTY2;
    }

    public double calculateRoomTimeConflictPenalty(Timetable timetable) {
        return countRoomTimeConflict(timetable) *  ROOM_TIME_CONFLICT_PENALTY;
    }

    public double calculateTeacherDayViolations(Timetable timetable) {
        return countTeacherDayTimeViolations(timetable) * TEACHER_DAY_TIME_CONFLICT_PENALTY ;
    }

    public double calculateSubGroupSubjectTeachingStyleConflictPenalty(Timetable timetable) {
        return countSubGroupSubjectTeachingStyleConflict(timetable) * SUBGROUP_SUBJECT_TEACHING_STYLE_CONFLICT_PENALTY;
    }

    public int countLateClassPeriod(Timetable timetable) {
        return (int) timetable.getTimeSlots().stream()
                .filter(t -> t.getClassPeriod() == ClassPeriod.PERIOD_6 && t.getTeachingStyle() == TeachingStyle.CURS)
                .count();
    }
    public int countFridayAfternoonClasses(Timetable timetable) {
        int classes = 0;
        List<Timeslot> timeslots = timetable.getTimeSlots();
        for (Timeslot timeslot : timeslots) {
            if (timeslot.getDayOfWeek() == DayOfWeek.VINERI && (
                    timeslot.getClassPeriod() == ClassPeriod.PERIOD_3 ||
                            timeslot.getClassPeriod() == ClassPeriod.PERIOD_4 ||
                            timeslot.getClassPeriod() == ClassPeriod.PERIOD_5 ||
                            timeslot.getClassPeriod() == ClassPeriod.PERIOD_6)) {
                classes++;
            }
        }
        return classes;
    }
    public int countConsecutiveDayConflicts(Timetable timetable) {
        int conflicts = 0;
        // Group timeslots by day of week
        Map<DayOfWeek, List<Timeslot>> dayTimeslotMap = timetable.getTimeSlots().stream()
                .collect(Collectors.groupingBy(Timeslot::getDayOfWeek));
        // Define the order of days as per your requirement
        DayOfWeek[] daysOfWeek = DayOfWeek.values();
        // Iterate through each day
        for (int i = 0; i < daysOfWeek.length - 1; i++) { // don't include the last day
            // Get timeslots for the current and next day
            List<Timeslot> timeslotsToday = dayTimeslotMap.get(daysOfWeek[i]);
            List<Timeslot> timeslotsNextDay = dayTimeslotMap.get(daysOfWeek[i+1]);
            if (timeslotsToday == null || timeslotsNextDay == null) continue;
            // Check if there is a timeslot in the last period of today
            boolean lastPeriodTodayExists = timeslotsToday.stream()
                    .anyMatch(t -> t.getClassPeriod() == ClassPeriod.PERIOD_6);
            // Check if there is a timeslot in the first period of the next day
            boolean firstPeriodNextDayExists = timeslotsNextDay.stream()
                    .anyMatch(t -> t.getClassPeriod() == ClassPeriod.PERIOD_1);
            // If both conditions are true, increment the conflicts
            if (lastPeriodTodayExists && firstPeriodNextDayExists) {
                conflicts++;
            }
        }
        // Return the number of conflicts
        return conflicts;
    }

    public int countCursLabCursConflicts(Timetable timetable) {
        int conflicts = 0;
        // Group timeslots by day of week
        Map<DayOfWeek, List<Timeslot>> dayTimeslotMap = timetable.getTimeSlots().stream()
                .collect(Collectors.groupingBy(Timeslot::getDayOfWeek));
        // Iterate through each day
        for (DayOfWeek day : dayTimeslotMap.keySet()) {
            // Get all timeslots for the day and sort them by class period
            List<Timeslot> timeslotsForDay = dayTimeslotMap.get(day).stream()
                    .sorted(Comparator.comparing(Timeslot::getClassPeriod))
                    .collect(Collectors.toList());

            // Iterate through the sorted timeslots and check for the CURS-LAB-CURS sequence
            for (int i = 0; i < timeslotsForDay.size() - 2; i++) {
                if (timeslotsForDay.get(i).getTeachingStyle() == TeachingStyle.CURS &&
                        timeslotsForDay.get(i+1).getTeachingStyle() == TeachingStyle.LABORATOR &&
                        timeslotsForDay.get(i+2).getTeachingStyle() == TeachingStyle.CURS) {
                    conflicts++;
                }
            }
        }
        return conflicts;
    }


    public int countTimeslotConflicts(Timetable timetable) {
        int conflicts = 0;
        List<Timeslot> timeslots = timetable.getTimeSlots();
        for (int i = 0; i < timeslots.size(); i++) {
            Timeslot timeslot1 = timeslots.get(i);
            for (int j = i + 1; j < timeslots.size(); j++) {
                Timeslot timeslot2 = timeslots.get(j);
                if (timeslot1.getDayOfWeek() == timeslot2.getDayOfWeek() &&
                        timeslot1.getClassPeriod() == timeslot2.getClassPeriod()) {
                    conflicts++;
                }
            }
        }
        return conflicts;
    }
    public int countSameSubjectTeachingStyleTimeConflicts(Timetable timetable) {
        int conflicts = 0;
        Map<Subject, List<Timeslot>> subjectTimeslotMap = timetable.getTimeSlots().stream()
                .collect(Collectors.groupingBy(Timeslot::getSubject));

        for (Subject subject : subjectTimeslotMap.keySet()) {
            List<Timeslot> timeslotsForSubject = subjectTimeslotMap.get(subject);

            Map<DayOfWeek, List<Timeslot>> dayTimeslotMap = timeslotsForSubject.stream()
                    .collect(Collectors.groupingBy(Timeslot::getDayOfWeek));

            for (DayOfWeek day : dayTimeslotMap.keySet()) {
                List<Timeslot> timeslotsForDay = dayTimeslotMap.get(day);

                Map<ClassPeriod, List<Timeslot>> periodTimeslotMap = timeslotsForDay.stream()
                        .collect(Collectors.groupingBy(Timeslot::getClassPeriod));

                for (ClassPeriod period : periodTimeslotMap.keySet()) {
                    List<Timeslot> timeslotsForPeriod = periodTimeslotMap.get(period);

                    Map<TeachingStyle, Long> styleCountMap = timeslotsForPeriod.stream()
                            .collect(Collectors.groupingBy(Timeslot::getTeachingStyle, Collectors.counting()));

                    if (styleCountMap.size() > 1) {
                        conflicts += styleCountMap.size() - 1;
                    }
                }
            }
        }
        return conflicts;
    }


    public int countSubGroupSubjectTeachingStyleConflict(Timetable timetable) {
        int conflicts = 0;
        Map<SubGroup, List<Timeslot>> subGroupTimeslotMap = timetable.getTimeSlots().stream()
                .filter(timeslot -> timeslot.getSubGroup() != null)
                .collect(Collectors.groupingBy(Timeslot::getSubGroup));

        for (SubGroup subGroup : subGroupTimeslotMap.keySet()) {
            List<Timeslot> timeslotsInSubGroup = subGroupTimeslotMap.get(subGroup);
            Map<Subject, List<Timeslot>> subjectTimeslotMap = timeslotsInSubGroup.stream()
                    .filter(timeslot -> timeslot.getSubject() != null)
                    .collect(Collectors.groupingBy(Timeslot::getSubject));

            for (Subject subject : subjectTimeslotMap.keySet()) {
                List<Timeslot> timeslotsInSubject = subjectTimeslotMap.get(subject);
                Map<TeachingStyle, Long> styleCountMap = timeslotsInSubject.stream()
                        .filter(timeslot -> timeslot.getTeachingStyle() != null)
                        .collect(Collectors.groupingBy(Timeslot::getTeachingStyle, Collectors.counting()));

                for (Long count : styleCountMap.values()) {
                    if (count > 1) {
                        conflicts += count - 1;
                    }
                }
            }
        }
        return conflicts;
    }

    public int countRoomTimeConflict(Timetable timetable) {
        int conflicts = 0;
        Map<Room, List<Timeslot>> roomTimeslotMap = timetable.getTimeSlots().stream()
                .collect(Collectors.groupingBy(Timeslot::getRoom));

        for (Room room : roomTimeslotMap.keySet()) {
            List<Timeslot> timeslotsInRoom = roomTimeslotMap.get(room);
            Map<DayOfWeek, List<Timeslot>> dayTimeslotMap = timeslotsInRoom.stream()
                    .collect(Collectors.groupingBy(Timeslot::getDayOfWeek));

            for (DayOfWeek day : dayTimeslotMap.keySet()) {
                List<Timeslot> timeslotsInDay = dayTimeslotMap.get(day);
                Map<ClassPeriod, Long> periodCountMap = timeslotsInDay.stream()
                        .collect(Collectors.groupingBy(Timeslot::getClassPeriod, Collectors.counting()));

                for (Long count : periodCountMap.values()) {
                    if (count > 1) {
                        conflicts += count - 1;
                    }
                }
            }
        }
        return conflicts;
    }

    public int countTeacherTimeConflict(Timetable timetable) {
        int conflicts = 0;
        Map<Teacher, List<Timeslot>> teacherTimeslotMap = timetable.getTimeSlots().stream()
                .collect(Collectors.groupingBy(Timeslot::getTeacher));

        for (Teacher teacher : teacherTimeslotMap.keySet()) {
            List<Timeslot> timeslotsForTeacher = teacherTimeslotMap.get(teacher);
            Map<DayOfWeek, List<Timeslot>> dayTimeslotMap = timeslotsForTeacher.stream()
                    .collect(Collectors.groupingBy(Timeslot::getDayOfWeek));

            for (DayOfWeek day : dayTimeslotMap.keySet()) {
                List<Timeslot> timeslotsInDay = dayTimeslotMap.get(day);
                Map<ClassPeriod, Long> periodCountMap = timeslotsInDay.stream()
                        .collect(Collectors.groupingBy(Timeslot::getClassPeriod, Collectors.counting()));

                for (Long count : periodCountMap.values()) {
                    if (count > 1) {
                        conflicts += count - 1;
                    }
                }
            }
        }
        return conflicts;
    }

    public int countSubGroupTimeConflict(Timetable timetable) {
        int conflicts = 0;
        List<Timeslot> timeslots = timetable.getTimeSlots().stream()
                .filter(timeslot -> timeslot.getSubGroup() != null && timeslot.getTeachingStyle() == TeachingStyle.LABORATOR)
                .collect(Collectors.toList());

        Map<SubGroup, List<Timeslot>> subGroupTimeslotMap = timeslots.stream()
                .collect(Collectors.groupingBy(Timeslot::getSubGroup));

        for (SubGroup subGroup : subGroupTimeslotMap.keySet()) {
            List<Timeslot> timeslotsForSubGroup = subGroupTimeslotMap.get(subGroup);
            Map<DayOfWeek, List<Timeslot>> dayTimeslotMap = timeslotsForSubGroup.stream()
                    .collect(Collectors.groupingBy(Timeslot::getDayOfWeek));

            for (DayOfWeek day : dayTimeslotMap.keySet()) {
                List<Timeslot> timeslotsInDay = dayTimeslotMap.get(day);
                Map<ClassPeriod, Long> periodCountMap = timeslotsInDay.stream()
                        .collect(Collectors.groupingBy(Timeslot::getClassPeriod, Collectors.counting()));

                for (Long count : periodCountMap.values()) {
                    if (count > 1) {
                        conflicts += count - 1;
                    }
                }
            }
        }
        return conflicts;
    }

    public int countGroupTimeConflict(Timetable timetable) {
        int conflicts = 0;
        List<Timeslot> timeslots = timetable.getTimeSlots().stream()
                .filter(timeslot -> timeslot.getGroup() != null && timeslot.getTeachingStyle() == TeachingStyle.SEMINAR)
                .collect(Collectors.toList());

        Map<Group, List<Timeslot>> groupTimeslotMap = timeslots.stream()
                .collect(Collectors.groupingBy(Timeslot::getGroup));

        for (Group group : groupTimeslotMap.keySet()) {
            List<Timeslot> timeslotsForGroup = groupTimeslotMap.get(group);
            Map<DayOfWeek, List<Timeslot>> dayTimeslotMap = timeslotsForGroup.stream()
                    .collect(Collectors.groupingBy(Timeslot::getDayOfWeek));

            for (DayOfWeek day : dayTimeslotMap.keySet()) {
                List<Timeslot> timeslotsInDay = dayTimeslotMap.get(day);
                Map<ClassPeriod, Long> periodCountMap = timeslotsInDay.stream()
                        .collect(Collectors.groupingBy(Timeslot::getClassPeriod, Collectors.counting()));

                for (Long count : periodCountMap.values()) {
                    if (count > 1) {
                        conflicts += count - 1;
                    }
                }
            }
        }
        return conflicts;
    }

    public int countStudyYearTimeConflict(Timetable timetable) {
        int conflicts = 0;
        List<Timeslot> timeslots = timetable.getTimeSlots().stream()
                .filter(timeslot -> timeslot.getTeachingStyle() == TeachingStyle.CURS)
                .collect(Collectors.toList());

        Map<StudyYear, List<Timeslot>> studyYearTimeslotMap = timeslots.stream()
                .collect(Collectors.groupingBy(Timeslot::getStudyYear));

        for (StudyYear studyYear : studyYearTimeslotMap.keySet()) {
            List<Timeslot> timeslotsForStudyYear = studyYearTimeslotMap.get(studyYear);
            Map<DayOfWeek, List<Timeslot>> dayTimeslotMap = timeslotsForStudyYear.stream()
                    .collect(Collectors.groupingBy(Timeslot::getDayOfWeek));

            for (DayOfWeek day : dayTimeslotMap.keySet()) {
                List<Timeslot> timeslotsInDay = dayTimeslotMap.get(day);
                Map<ClassPeriod, Long> periodCountMap = timeslotsInDay.stream()
                        .collect(Collectors.groupingBy(Timeslot::getClassPeriod, Collectors.counting()));

                for (Long count : periodCountMap.values()) {
                    if (count > 1) {
                        conflicts += count - 1;
                    }
                }
            }
        }
        return conflicts;
    }

    public int countTeachingOrderViolations1(Timetable timetable) {
        int totalViolations = 0;

        Map<Subject, List<Timeslot>> subjectTimeslotsMap = timetable.getTimeSlots().stream()
                .collect(Collectors.groupingBy(Timeslot::getSubject));

        for (Map.Entry<Subject, List<Timeslot>> entry : subjectTimeslotsMap.entrySet()) {
            Subject subject = entry.getKey();
            List<Timeslot> timeslots = entry.getValue();

            boolean hasCurs = timeslots.stream().anyMatch(ts -> ts.getTeachingStyle() == TeachingStyle.CURS);
            boolean hasSeminar = timeslots.stream().anyMatch(ts -> ts.getTeachingStyle() == TeachingStyle.SEMINAR);
            if (!hasCurs || !hasSeminar) {
                continue;
            }
            Timeslot cursTimeslot = timeslots.stream()
                    .filter(ts -> ts.getTeachingStyle() == TeachingStyle.CURS)
                    .findFirst()
                    .orElse(null);
            Map<Group, List<Timeslot>> groupTimeslotsMap = timeslots.stream()
                    .filter(ts -> ts.getTeachingStyle() == TeachingStyle.SEMINAR)
                    .collect(Collectors.groupingBy(Timeslot::getGroup));
            for (List<Timeslot> groupTimeslots : groupTimeslotsMap.values()) {
                if (groupTimeslots.stream().anyMatch(ts -> isBefore(ts, cursTimeslot))) {
                    totalViolations++;
                }
            }
        }
        return totalViolations;
    }

    public int countTeachingOrderViolations2(Timetable timetable) {
        int totalViolations = 0;

        Map<Subject, List<Timeslot>> subjectTimeslotsMap = timetable.getTimeSlots().stream()
                .collect(Collectors.groupingBy(Timeslot::getSubject));

        for (Map.Entry<Subject, List<Timeslot>> entry : subjectTimeslotsMap.entrySet()) {
            Subject subject = entry.getKey();
            List<Timeslot> timeslots = entry.getValue();

            boolean hasCurs = timeslots.stream().anyMatch(ts -> ts.getTeachingStyle() == TeachingStyle.CURS);
            boolean hasLaborator = timeslots.stream().anyMatch(ts -> ts.getTeachingStyle() == TeachingStyle.LABORATOR);
            if (!hasCurs || !hasLaborator) {
                continue;
            }

            Timeslot cursTimeslot = timeslots.stream()
                    .filter(ts -> ts.getTeachingStyle() == TeachingStyle.CURS)
                    .findFirst()
                    .orElse(null);
            Map<SubGroup, List<Timeslot>> subGroupTimeslotsMap = timeslots.stream()
                    .filter(ts -> ts.getTeachingStyle() == TeachingStyle.LABORATOR)
                    .collect(Collectors.groupingBy(Timeslot::getSubGroup));
            for (List<Timeslot> subGroupTimeslots : subGroupTimeslotsMap.values()) {
                if (subGroupTimeslots.stream().anyMatch(ts -> isBefore(ts, cursTimeslot))) {
                    totalViolations++;
                }
            }
        }

        return totalViolations;
    }

    private boolean isBefore(Timeslot ts1, Timeslot ts2) {
        DayOfWeek day1 = ts1.getDayOfWeek();
        DayOfWeek day2 = ts2.getDayOfWeek();
        ClassPeriod period1 = ts1.getClassPeriod();
        ClassPeriod period2 = ts2.getClassPeriod();

        if (day1.ordinal() < day2.ordinal()) {
            return true;
        } else if (day1.ordinal() == day2.ordinal()) {
            return period1.ordinal() < period2.ordinal();
        } else {
            return false;
        }
    }

    public int countRoomDayViolations(Timetable timetable) {
        int violations = 0;
        List<Timeslot> timeslots = timetable.getTimeSlots();
        for (Timeslot timeslot : timeslots) {
            if (timeslot.getRoom().getId() == 402 && timeslot.getDayOfWeek() == DayOfWeek.VINERI) {
                violations++;
            }
        }
        return violations;
    }

    public int countTeacherDayTimeViolations(Timetable timetable) {
        int violations = 0;
        List<Timeslot> timeslots = timetable.getTimeSlots();
        for (Timeslot timeslot : timeslots) {
            if (timeslot.getTeacher().getId() == 2
                    && (timeslot.getClassPeriod() == ClassPeriod.PERIOD_1
                    || timeslot.getClassPeriod() == ClassPeriod.PERIOD_2
                    || timeslot.getClassPeriod() == ClassPeriod.PERIOD_3)
                    && timeslot.getDayOfWeek() == DayOfWeek.LUNI) {
                violations++;
            }
        }
        return violations;
    }
}