package com.licenta.aplicatie.schedule;

import com.licenta.aplicatie.models.*;
import com.licenta.aplicatie.repository.TimeslotRepository;
import com.licenta.aplicatie.repository.TimetableRepository;
import com.licenta.aplicatie.service.TimeslotService;
import com.licenta.aplicatie.service.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GeneticAlgorithm {


    private static int nextTimetableId = 1;

    @Autowired
    TimeslotService timeslotService;
    @Autowired
    TimetableRepository timetableRepository;

    @Autowired
    TimeslotRepository timeslotRepository;
    private FitnessFunction fitnessFunction;

    private Population population;

    public GeneticAlgorithm() {
        this.fitnessFunction = new FitnessFunction();
    }

    private static final int POPULATION_SIZE = 5;

    private static final int MAX_GENERATIONS = 5;

    public void run() {
        initializePopulation();
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            calculateFitness();
            selection();
        }
    }
    public void initializePopulation() {
        population = new Population();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Timetable timetable = timeslotService.generateRandomTimeslots();
            population.addTimetable(timetable);
        }
    }
    public void calculateFitness() {
        List<Timetable> allTimetables = population.getTimetables();
        for (Timetable timetable : allTimetables) {
            double fitness = fitnessFunction.calculateFitness(timetable);
            timetable.setFitness(fitness);
            timetableRepository.save(timetable);
        }
        Collections.sort(population.getTimetables(), Comparator.comparing(Timetable::getFitness).reversed());
    }

    int ELITE_SIZE = 2;
    private Timetable tournamentSelection() {
        int tournamentSize = 5;
        Population tournament = new Population();
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * population.getTimetables().size());
            System.out.println("(randomId)"+randomId);
            tournament.addTimetable(population.getTimetables().get(randomId));
        }
        return tournament.getBestTimetable();
    }
    private void selection() {
        System.out.println("(Selection)");
        Population newPopulation = new Population();
        for (int i = 0; i < ELITE_SIZE; i++) {
            newPopulation.addTimetable(population.getTimetables().get(i));
        }
        while (newPopulation.getTimetables().size() < POPULATION_SIZE) {
            Timetable parent1 = tournamentSelection();
            Timetable parent2;
            int maxAttempts = 100;
            int attempts = 0;
            do {
                parent2 = tournamentSelection();
                attempts++;
                if (attempts > maxAttempts) {
                    break;
                }
            } while (parent1.equals(parent2));

            Timetable child = Crossover(parent1, parent2);
            child.setId(getNextTimetableId());
            mutation(child);
            newPopulation.addTimetable(child);
        }
        population = newPopulation;
    }

    private int getNextTimetableId() {
        return nextTimetableId++;
    }

    private Timetable Crossover(Timetable parent1, Timetable parent2) {

        Random rand = new Random();
        List<Timeslot> parent1CursTimeslots = parent1.getTimeSlots().stream()
                .filter(t -> t.getTeachingStyle() == TeachingStyle.CURS)
                .collect(Collectors.toList());
        List<Timeslot> parent2CursTimeslots = parent2.getTimeSlots().stream()
                .filter(t -> t.getTeachingStyle() == TeachingStyle.CURS)
                .collect(Collectors.toList());

        int minSize = Math.min(parent1CursTimeslots.size(), parent2CursTimeslots.size());
        Timetable child = new Timetable();
        for (int i = 0; i < minSize; i++) {
            Timeslot parent1Timeslot = parent1CursTimeslots.get(i);
            Timeslot parent2Timeslot = parent2CursTimeslots.get(i);
            Timeslot childTimeslot = new Timeslot();

            childTimeslot.setTeacher(rand.nextBoolean() ? parent1Timeslot.getTeacher() : parent2Timeslot.getTeacher());

            childTimeslot.setRoom(rand.nextBoolean() ? parent1Timeslot.getRoom() : parent2Timeslot.getRoom());

            childTimeslot.setDayOfWeek(rand.nextBoolean() ? parent1Timeslot.getDayOfWeek() : parent2Timeslot.getDayOfWeek());

            childTimeslot.setClassPeriod(rand.nextBoolean() ? parent1Timeslot.getClassPeriod() : parent2Timeslot.getClassPeriod());

            childTimeslot.setSubject(parent1Timeslot.getSubject());
            childTimeslot.setTeachingStyle(TeachingStyle.CURS);
            childTimeslot.setStudyYear(parent1Timeslot.getStudyYear());
            childTimeslot.setGroup(null);
            childTimeslot.setSubGroup(null);
            child.addTimeslot(childTimeslot);
        }
///////////////////////////////////////////////////////////////////////////
        Map<Subject, Map<Group, List<Timeslot>>> parent1SeminarTimeslotsBySubjectAndGroup = parent1.getTimeSlots().stream()
                .filter(t -> t.getTeachingStyle() == TeachingStyle.SEMINAR)
                .collect(Collectors.groupingBy(Timeslot::getSubject,
                        Collectors.groupingBy(Timeslot::getGroup)));

        Map<Subject, Map<Group, List<Timeslot>>> parent2SeminarTimeslotsBySubjectAndGroup = parent2.getTimeSlots().stream()
                .filter(t -> t.getTeachingStyle() == TeachingStyle.SEMINAR)
                .collect(Collectors.groupingBy(Timeslot::getSubject,
                        Collectors.groupingBy(Timeslot::getGroup)));

        for (Subject subject : parent1SeminarTimeslotsBySubjectAndGroup.keySet()) {
            if (!parent2SeminarTimeslotsBySubjectAndGroup.containsKey(subject)) {
                continue;
            }
            Map<Group, List<Timeslot>> parent1GroupTimeslots = parent1SeminarTimeslotsBySubjectAndGroup.get(subject);
            Map<Group, List<Timeslot>> parent2GroupTimeslots = parent2SeminarTimeslotsBySubjectAndGroup.get(subject);
            for (Group group : parent1GroupTimeslots.keySet()) {
                if (!parent2GroupTimeslots.containsKey(group)) {
                    continue;
                }
                List<Timeslot> parent1Timeslots = parent1GroupTimeslots.get(group);
                List<Timeslot> parent2Timeslots = parent2GroupTimeslots.get(group);
                for (int i = 0; i < Math.min(parent1Timeslots.size(), parent2Timeslots.size()); i++) {
                    Timeslot parent1Timeslot = parent1Timeslots.get(i);
                    Timeslot parent2Timeslot = parent2Timeslots.get(i);
                    Timeslot childTimeslot = new Timeslot();

                    childTimeslot.setTeacher(rand.nextBoolean() ? parent1Timeslot.getTeacher() : parent2Timeslot.getTeacher());
                    childTimeslot.setRoom(rand.nextBoolean() ? parent1Timeslot.getRoom() : parent2Timeslot.getRoom());
                    childTimeslot.setDayOfWeek(rand.nextBoolean() ? parent1Timeslot.getDayOfWeek() : parent2Timeslot.getDayOfWeek());
                    childTimeslot.setClassPeriod(rand.nextBoolean() ? parent1Timeslot.getClassPeriod() : parent2Timeslot.getClassPeriod());
                    childTimeslot.setSubject(subject);
                    childTimeslot.setGroup(group);
                    childTimeslot.setTeachingStyle(TeachingStyle.SEMINAR);
                    childTimeslot.setStudyYear(null);
                    childTimeslot.setSubGroup(null);
                    child.addTimeslot(childTimeslot);
                }
            }
        }
/////////////////////////////////////////////////////////////////////////////////////
        Map<Subject, Map<SubGroup, List<Timeslot>>> parent1LaboratorTimeslotsBySubjectAndSubGroup = parent1.getTimeSlots().stream()
                .filter(t -> t.getTeachingStyle() == TeachingStyle.LABORATOR)
                .collect(Collectors.groupingBy(Timeslot::getSubject,
                        Collectors.groupingBy(Timeslot::getSubGroup)));

        Map<Subject, Map<SubGroup, List<Timeslot>>> parent2LaboratorTimeslotsBySubjectAndSubGroup = parent2.getTimeSlots().stream()
                .filter(t -> t.getTeachingStyle() == TeachingStyle.LABORATOR)
                .collect(Collectors.groupingBy(Timeslot::getSubject,
                        Collectors.groupingBy(Timeslot::getSubGroup)));

        for (Subject subject : parent1LaboratorTimeslotsBySubjectAndSubGroup.keySet()) {
            if (!parent2LaboratorTimeslotsBySubjectAndSubGroup.containsKey(subject)) {
                continue;
            }
            Map<SubGroup, List<Timeslot>> parent1SubGroupTimeslots = parent1LaboratorTimeslotsBySubjectAndSubGroup.get(subject);
            Map<SubGroup, List<Timeslot>> parent2SubGroupTimeslots = parent2LaboratorTimeslotsBySubjectAndSubGroup.get(subject);
            for (SubGroup subGroup : parent1SubGroupTimeslots.keySet()) {
                if (!parent2SubGroupTimeslots.containsKey(subGroup)) {
                    continue;
                }
                List<Timeslot> parent1Timeslots = parent1SubGroupTimeslots.get(subGroup);
                List<Timeslot> parent2Timeslots = parent2SubGroupTimeslots.get(subGroup);
                for (int i = 0; i < Math.min(parent1Timeslots.size(), parent2Timeslots.size()); i++) {
                    Timeslot parent1Timeslot = parent1Timeslots.get(i);
                    Timeslot parent2Timeslot = parent2Timeslots.get(i);
                    Timeslot childTimeslot = new Timeslot();
                    childTimeslot.setTeacher(rand.nextBoolean() ? parent1Timeslot.getTeacher() : parent2Timeslot.getTeacher());
                    childTimeslot.setRoom(rand.nextBoolean() ? parent1Timeslot.getRoom() : parent2Timeslot.getRoom());
                    childTimeslot.setDayOfWeek(rand.nextBoolean() ? parent1Timeslot.getDayOfWeek() : parent2Timeslot.getDayOfWeek());
                    childTimeslot.setClassPeriod(rand.nextBoolean() ? parent1Timeslot.getClassPeriod() : parent2Timeslot.getClassPeriod());
                    childTimeslot.setSubject(subject);
                    childTimeslot.setGroup(null);
                    childTimeslot.setSubGroup(subGroup);
                    childTimeslot.setTeachingStyle(TeachingStyle.LABORATOR);
                    childTimeslot.setStudyYear(null);
                    child.addTimeslot(childTimeslot);
                }
            }
        }
        return child;
    }


    private static final double MUTATION_RATE = 0.05;
    public void mutation(Timetable timetable) {
        System.out.println("(Mutation)");
        Random rand = new Random();
        List<Timeslot> cursTimeslots = timetable.getTimeSlots().stream()
                .filter(t -> t.getTeachingStyle() == TeachingStyle.CURS)
                .collect(Collectors.toList());
        if (cursTimeslots.size() >= 2 && rand.nextDouble() < MUTATION_RATE) {
            swapTimeslots(rand, cursTimeslots, timetable);
        }

        Map<Group, List<Timeslot>> seminarTimeslotsByGroup = timetable.getTimeSlots().stream()
                .filter(t -> t.getTeachingStyle() == TeachingStyle.SEMINAR)
                .collect(Collectors.groupingBy(Timeslot::getGroup));
        for (List<Timeslot> seminarTimeslots : seminarTimeslotsByGroup.values()) {
            if (seminarTimeslots.size() >= 2 && rand.nextDouble() < MUTATION_RATE) {
                swapTimeslots(rand, seminarTimeslots, timetable);
            }
        }

        Map<SubGroup, List<Timeslot>> labTimeslotsBySubGroup = timetable.getTimeSlots().stream()
                .filter(t -> t.getTeachingStyle() == TeachingStyle.LABORATOR)
                .collect(Collectors.groupingBy(Timeslot::getSubGroup));
        for (List<Timeslot> labTimeslots : labTimeslotsBySubGroup.values()) {
            if (labTimeslots.size() >= 2 && rand.nextDouble() < MUTATION_RATE) {
                swapTimeslots(rand, labTimeslots, timetable);
            }
        }
    }

    private void swapTimeslots(Random rand, List<Timeslot> timeslots, Timetable timetable) {
        int index1 = rand.nextInt(timeslots.size());
        int index2;
        do {
            index2 = rand.nextInt(timeslots.size());
        } while (index1 == index2);
        Timeslot timeslot1 = timeslots.get(index1);
        Timeslot timeslot2 = timeslots.get(index2);
        DayOfWeek day1 = timeslot1.getDayOfWeek();
        ClassPeriod period1 = timeslot1.getClassPeriod();
        DayOfWeek day2 = timeslot2.getDayOfWeek();
        ClassPeriod period2 = timeslot2.getClassPeriod();
        timeslot1.setDayOfWeek(day2);
        timeslot1.setClassPeriod(period2);
        timeslot2.setDayOfWeek(day1);
        timeslot2.setClassPeriod(period1);

        boolean validMutation;
        switch (timeslot1.getTeachingStyle()) {
            case CURS:
                validMutation = validateMutationCurs(timetable, timeslot1, timeslot2);
                break;
            case SEMINAR:
                validMutation = validateMutationSeminar(timetable, timeslot1, timeslot2); // to be implemented
                break;
            case LABORATOR:
                validMutation = validateMutationLab(timetable, timeslot1, timeslot2); // to be implemented
                break;
            default:
                throw new IllegalArgumentException("Unexpected teaching style: " + timeslot1.getTeachingStyle());
        }

        if (!validMutation) {
            timeslot1.setDayOfWeek(day1);
            timeslot1.setClassPeriod(period1);
            timeslot2.setDayOfWeek(day2);
            timeslot2.setClassPeriod(period2);
        }
    }


    private boolean validateMutationCurs(Timetable timetable, Timeslot timeslot1, Timeslot timeslot2) {
        DayOfWeek day1 = timeslot1.getDayOfWeek();
        ClassPeriod period1 = timeslot1.getClassPeriod();
        DayOfWeek day2 = timeslot2.getDayOfWeek();
        ClassPeriod period2 = timeslot2.getClassPeriod();

        Room room402 = timeslot1.getRoom();

        for (Timeslot timeslot : timetable.getTimeSlots()) {
            if (!timeslot.equals(timeslot1) && !timeslot.equals(timeslot2)) {
                if ((timeslot.getTeacher().equals(timeslot1.getTeacher()) && timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2)) ||
                        (timeslot.getTeacher().equals(timeslot2.getTeacher()) && timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1))) {
                    return false;
                }
                if ((timeslot.getRoom().equals(room402) && timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2)) ||
                        (timeslot.getRoom().equals(room402) && timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1))) {
                    return false;
                }
            }
        }

        return true;
    }
    private boolean validateMutationSeminar(Timetable timetable, Timeslot timeslot1, Timeslot timeslot2) {
        DayOfWeek day1 = timeslot1.getDayOfWeek();
        ClassPeriod period1 = timeslot1.getClassPeriod();
        DayOfWeek day2 = timeslot2.getDayOfWeek();
        ClassPeriod period2 = timeslot2.getClassPeriod();

        Room room354 = timeslot1.getRoom();

        for (Timeslot timeslot : timetable.getTimeSlots()) {
            if (!timeslot.equals(timeslot1) && !timeslot.equals(timeslot2)) {
                if ((timeslot.getTeacher().equals(timeslot1.getTeacher()) && timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2)) ||
                        (timeslot.getTeacher().equals(timeslot2.getTeacher()) && timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1))) {
                    return false;
                }
                if ((timeslot.getRoom().equals(room354) && timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2)) ||
                        (timeslot.getRoom().equals(room354) && timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1))) {
                    return false;
                }

                if ((timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1)) ||
                        (timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2))) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean validateMutationLab(Timetable timetable, Timeslot timeslot1, Timeslot timeslot2) {
        DayOfWeek day1 = timeslot1.getDayOfWeek();
        ClassPeriod period1 = timeslot1.getClassPeriod();
        DayOfWeek day2 = timeslot2.getDayOfWeek();
        ClassPeriod period2 = timeslot2.getClassPeriod();

        for (Timeslot timeslot : timetable.getTimeSlots()) {
            if (!timeslot.equals(timeslot1) && !timeslot.equals(timeslot2)) {
                // Checking for teacher conflict
                if ((timeslot.getTeacher().equals(timeslot1.getTeacher()) && timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2)) ||
                        (timeslot.getTeacher().equals(timeslot2.getTeacher()) && timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1))) {
                    return false;
                }
                // Checking for room conflict
                if ((timeslot.getRoom().equals(timeslot1.getRoom()) && timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2)) ||
                        (timeslot.getRoom().equals(timeslot2.getRoom()) && timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1))) {
                    return false;
                }
                // Additional check to see if there are any timeslots that conflict with the new times
                if ((timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1)) ||
                        (timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2))) {
                    return false;
                }
            }
        }

        return true;
    }



}

//    private void swapTimeslots(Random rand, List<Timeslot> timeslots, Timetable timetable) {
//        int index1 = rand.nextInt(timeslots.size());
//        int index2;
//        do {
//            index2 = rand.nextInt(timeslots.size());
//        } while (index1 == index2);
//        Timeslot timeslot1 = timeslots.get(index1);
//        Timeslot timeslot2 = timeslots.get(index2);
//        DayOfWeek day1 = timeslot1.getDayOfWeek();
//        ClassPeriod period1 = timeslot1.getClassPeriod();
//        DayOfWeek day2 = timeslot2.getDayOfWeek();
//        ClassPeriod period2 = timeslot2.getClassPeriod();
//        timeslot1.setDayOfWeek(day2);
//        timeslot1.setClassPeriod(period2);
//        timeslot2.setDayOfWeek(day1);
//        timeslot2.setClassPeriod(period1);
//        if (!validateMutation(timetable, timeslot1, timeslot2)) {
//            timeslot1.setDayOfWeek(day1);
//            timeslot1.setClassPeriod(period1);
//            timeslot2.setDayOfWeek(day2);
//            timeslot2.setClassPeriod(period2);
//        }
//    }
//
//    private boolean validateMutation(Timetable timetable, Timeslot timeslot1, Timeslot timeslot2) {
//        DayOfWeek day1 = timeslot1.getDayOfWeek();
//        ClassPeriod period1 = timeslot1.getClassPeriod();
//        DayOfWeek day2 = timeslot2.getDayOfWeek();
//        ClassPeriod period2 = timeslot2.getClassPeriod();
//        for (Timeslot timeslot : timetable.getTimeSlots()) {
//            if (!timeslot.equals(timeslot1) && !timeslot.equals(timeslot2))
//            {
//                if ((timeslot.getTeacher().equals(timeslot1.getTeacher()) && timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2)) ||
//                        (timeslot.getTeacher().equals(timeslot2.getTeacher()) && timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1)) ||
//                        (timeslot.getRoom().equals(timeslot1.getRoom()) && timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2)) ||
//                        (timeslot.getRoom().equals(timeslot2.getRoom()) && timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1)) ||
//                        (Objects.equals(timeslot.getGroup(), timeslot1.getGroup()) && timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2)) ||
//                        (Objects.equals(timeslot.getGroup(), timeslot2.getGroup()) && timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1)) ||
//                        (Objects.equals(timeslot.getSubGroup(), timeslot1.getSubGroup()) && timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2)) ||
//                        (Objects.equals(timeslot.getSubGroup(), timeslot2.getSubGroup()) && timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1)) ||
//                        (Objects.equals(timeslot.getStudyYear(), timeslot1.getStudyYear()) && timeslot.getDayOfWeek().equals(day2) && timeslot.getClassPeriod().equals(period2)) ||
//                        (Objects.equals(timeslot.getStudyYear(), timeslot2.getStudyYear()) && timeslot.getDayOfWeek().equals(day1) && timeslot.getClassPeriod().equals(period1))) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

//    public void run() {
//        initializePopulation();
////        calculateFitness();
//        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
//            calculateFitness();
//            selection();
//            //calculateFitness();
//        }
//        //persistFinalPopulation();
//    }

//    public void persistFinalPopulation() {
//        List<Timetable> finalPopulation = population.getTimetables();
//        timetableRepository.deleteAll();
//        for (Timetable timetable : finalPopulation) {
//            timetableRepository.save(timetable);
//        }
//    }