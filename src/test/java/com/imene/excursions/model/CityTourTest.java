package com.imene.excursions.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CityTourTest {

    private static final LocalDate EXCURSION_DATE = LocalDate.of(2026, 12, 15);

    private CityTour cityTour;

    @BeforeEach
    void setUp() {
        cityTour = new CityTour("EXC30", "Visite d'Arusha", EXCURSION_DATE, 40.0, 1, 15);
    }

    @Test
    void shouldApplyDiscountForChildUnderTwelve() {
        Customer child = new Customer("CUS01", "Marie", LocalDate.of(2015, 1, 1), Level.BEGINNER);

        assertEquals(20.0, cityTour.calculatePrice(child));
    }

    @Test
    void shouldNotApplyDiscountOnTwelfthBirthday() {
        Customer child = new Customer("CUS02", "Paola", LocalDate.of(2014, 12, 15), Level.BEGINNER);

        assertEquals(40.0, cityTour.calculatePrice(child));
    }

    @Test
    void shouldChargeFullPriceForAdult() {
        Customer adult = new Customer("CUS03", "Imène", LocalDate.of(2003, 10, 4), Level.BEGINNER);

        assertEquals(40.0, cityTour.calculatePrice(adult));
    }

    @Test
    void shouldAcceptEveryone() {
        Customer baby = new Customer("CUS04", "Nelly", LocalDate.of(2025, 3, 1), Level.BEGINNER);

        assertDoesNotThrow(() -> cityTour.checkEligibility(baby));
    }
}