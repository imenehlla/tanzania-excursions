package com.imene.excursions.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ExcursionTest {

    private static final LocalDate EXCURSION_DATE = LocalDate.of(2026, 12, 15);

    private Excursion excursion;

    @BeforeEach
    void setUp() {
        // On utilise CityTour car Excursion est abstraite
        excursion = new CityTour("EXC01", "Visite d'Arusha", EXCURSION_DATE, 40.0, 1, 2);
    }

    @Test
    void shouldAddParticipantWhenPlacesAreAvailable() {
        excursion.addParticipant();

        assertEquals(1, excursion.getParticipantCount());
    }

    @Test
    void shouldBeFullWhenMaxParticipantsIsReached() {
        excursion.addParticipant();
        excursion.addParticipant();

        assertTrue(excursion.isFull());
    }

    @Test
    void shouldRefuseParticipantWhenExcursionIsFull() {
        excursion.addParticipant();
        excursion.addParticipant();

        assertThrows(IllegalStateException.class, () -> excursion.addParticipant());
        assertEquals(2, excursion.getParticipantCount());
    }

    @Test
    void shouldRefuseParticipantWhenExcursionIsCancelled() {
        excursion.cancel();

        assertThrows(IllegalStateException.class, () -> excursion.addParticipant());
    }

    @Test
    void shouldNotHaveEnoughParticipantsBelowMinimum() {
        Excursion tour = new CityTour("EXC02", "Visite de Moshi", EXCURSION_DATE, 30.0, 3, 10);
        tour.addParticipant();
        tour.addParticipant();

        assertFalse(tour.hasEnoughParticipants());
    }

    @Test
    void shouldBePastWhenDateIsBeforeToday() {
        assertTrue(excursion.isPast(EXCURSION_DATE.plusDays(1)));
    }

    @Test
    void shouldNotBePastOnTheDayOfTheExcursion() {
        assertFalse(excursion.isPast(EXCURSION_DATE));
    }

    @Test
    void shouldRejectMaxParticipantsLowerThanMin() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new CityTour("EXC03", "Visite", EXCURSION_DATE, 30.0, 5, 2));

        assertEquals("Le maximum de participants (2) doit être supérieur ou égal au minimum (5)",
                exception.getMessage());
    }

    @Test
    void shouldRejectNegativeBasePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new CityTour("EXC04", "Visite", EXCURSION_DATE, -10.0, 1, 5));
    }
}