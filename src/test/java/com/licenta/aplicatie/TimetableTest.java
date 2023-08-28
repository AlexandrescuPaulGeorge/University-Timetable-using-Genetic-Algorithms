package com.licenta.aplicatie;

import com.licenta.aplicatie.schedule.FitnessFunction;
import com.licenta.aplicatie.schedule.Timeslot;
import com.licenta.aplicatie.schedule.Timetable;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class TimetableTest {

    @Test
    void testCalculateFitness() {
        // Arrange
        Timetable timetable = new Timetable();
        Timeslot timeslot = new Timeslot();  // adjust this line to create a Timeslot object if needed
        timetable.addTimeslot(timeslot);

        FitnessFunction mockFitnessFunction = Mockito.mock(FitnessFunction.class);
        Mockito.when(mockFitnessFunction.calculateFitness(Mockito.any(Timetable.class))).thenReturn(1.0);
    }

    @Test
    void testAddTimeslot() {

        Timetable timetable = new Timetable();
        Timeslot timeslot = new Timeslot();
        timetable.addTimeslot(timeslot);

        assertTrue(timetable.getTimeSlots().contains(timeslot), "Timeslot should be added to timetable");
    }

    @Test
    void testCopyConstructor() {
        // Arrange
        Timetable original = new Timetable();
        Timeslot timeslot = new Timeslot();
        original.addTimeslot(timeslot);

        Timetable copy = new Timetable(original);

        assertEquals(original.getTimeSlots(), copy.getTimeSlots(), "Timetables should have the same timeslots");
        assertEquals(original.getFitness(), copy.getFitness(), "Timetables should have the same fitness");

    }
}


