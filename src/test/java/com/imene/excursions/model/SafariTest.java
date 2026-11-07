package com.imene.excursions.model;

import com.imene.excursions.exception.ConditionNotMetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SafariTest {

    private static final LocalDate EXCURSION_DATE = LocalDate.of(2026, 12, 15);

    private Safari safari;

    @BeforeEach
    void setUp() {
        safari = new Safari("EXC20", "Safari à Tarangire", EXCURSION_DATE, 120.0, 2, 6, 70.0);
    }

    @Test
    void shouldAcceptChildWhoIsExactlyMinimumAge() {
        Customer child = new Customer("CUS01", "Marie", LocalDate.of(2020, 12, 15), Level.BEGINNER);

        assertDoesNotThrow(() -> safari.checkEligibility(child));
    }

    @Test
    void shouldRefuseChildBelowMinimumAge() {
        Customer child = new Customer("CUS02", "Paola", LocalDate.of(2020, 12, 16), Level.BEGINNER);

        assertThrows(ConditionNotMetException.class, () -> safari.checkEligibility(child));
    }

    @Test
    void shouldAddParkFeesAndLunchBoxToBasePrice() {
        Customer adult = new Customer("CUS03", "Imène", LocalDate.of(2003, 10, 4), Level.BEGINNER);

        assertEquals(120.0 + 70.0 + MealIncluded.LUNCH_BOX_PRICE, safari.calculatePrice(adult));
    }

    @Test
    void shouldRejectNegativeParkFees() {
        assertThrows(IllegalArgumentException.class,
                () -> new Safari("EXC21", "Safari", EXCURSION_DATE, 120.0, 2, 6, -5.0));
    }
}