package com.imene.excursions.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private final Customer customer =
            new Customer("CUS01", "Imène", LocalDate.of(2003, 10, 4), Level.INTERMEDIATE);

    @Test
    void shouldNotCountBirthdayBeforeItHappens() {
        assertEquals(22, customer.getAgeOn(LocalDate.of(2026, 10, 3)));
    }

    @Test
    void shouldCountBirthdayOnTheDay() {
        assertEquals(23, customer.getAgeOn(LocalDate.of(2026, 10, 4)));
    }

    @Test
    void shouldHaveLevelWhenSameAsRequired() {
        assertTrue(customer.hasAtLeastLevel(Level.INTERMEDIATE));
    }

    @Test
    void shouldHaveLevelWhenLowerIsRequired() {
        assertTrue(customer.hasAtLeastLevel(Level.BEGINNER));
    }

    @Test
    void shouldNotHaveLevelWhenHigherIsRequired() {
        assertFalse(customer.hasAtLeastLevel(Level.ADVANCED));
    }

    @Test
    void shouldRejectEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer("CUS02", " ", LocalDate.of(2000, 1, 1), Level.BEGINNER));
    }
}