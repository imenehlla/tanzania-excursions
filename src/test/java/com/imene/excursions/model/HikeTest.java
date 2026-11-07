package com.imene.excursions.model;

import com.imene.excursions.exception.ConditionNotMetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HikeTest {

    private static final LocalDate EXCURSION_DATE = LocalDate.of(2026, 12, 15);

    private Hike kilimanjaroHike;
    private Hike waterfallHike;

    @BeforeEach
    void setUp() {
        kilimanjaroHike = new Hike("EXC10", "Journée sur le Kilimandjaro", EXCURSION_DATE,
                100.0, 2, 8, Level.ADVANCED);
        waterfallHike = new Hike("EXC11", "Cascades de Materuni", EXCURSION_DATE,
                30.0, 1, 12, Level.BEGINNER);
    }

    private Customer customerWithLevel(Level level) {
        return new Customer("CUS01", "Imène", LocalDate.of(2003, 10, 4), level);
    }

    @Test
    void shouldAcceptCustomerWithRequiredLevel() {
        Customer customer = customerWithLevel(Level.ADVANCED);

        assertDoesNotThrow(() -> kilimanjaroHike.checkEligibility(customer));
    }

    @Test
    void shouldAcceptCustomerWithHigherLevel() {
        Customer customer = customerWithLevel(Level.ADVANCED);

        assertDoesNotThrow(() -> waterfallHike.checkEligibility(customer));
    }

    @Test
    void shouldRefuseCustomerWithInsufficientLevel() {
        Customer customer = customerWithLevel(Level.BEGINNER);

        assertThrows(ConditionNotMetException.class, () -> kilimanjaroHike.checkEligibility(customer));
    }

    @Test
    void shouldAddLunchBoxToBasePrice() {
        Customer customer = customerWithLevel(Level.BEGINNER);

        assertEquals(30.0 + MealIncluded.LUNCH_BOX_PRICE, waterfallHike.calculatePrice(customer));
    }

    @Test
    void shouldIncludePortersForDifficultHike() {
        assertTrue(kilimanjaroHike.getInclusions().contains(Inclusion.PORTERS));
    }

    @Test
    void shouldNotIncludePortersForEasyHike() {
        assertFalse(waterfallHike.getInclusions().contains(Inclusion.PORTERS));
    }
}