package com.imene.excursions.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    private Customer customer;
    private Excursion excursion;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        customer = new Customer("CUS01", "Imène", LocalDate.of(2003, 10, 4), Level.BEGINNER);
        excursion = new CityTour("EXC30", "Visite d'Arusha", LocalDate.of(2026, 12, 15), 40.0, 1, 15);
        reservation = new Reservation("RES01", customer, excursion, LocalDate.of(2026, 11, 1));
    }

    @Test
    void shouldBeConfirmedWhenCreated() {
        assertTrue(reservation.isConfirmed());
    }

    @Test
    void shouldStoreExcursionPriceForCustomer() {
        assertEquals(40.0, reservation.getPrice());
    }

    @Test
    void shouldBeCancelledAfterCancel() {
        reservation.cancel();

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
    }

    @Test
    void shouldRefuseCancellingTwice() {
        reservation.cancel();

        assertThrows(IllegalStateException.class, () -> reservation.cancel());
    }

    @Test
    void shouldRejectReservationWithoutCustomer() {
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation("RES02", null, excursion, LocalDate.of(2026, 11, 1)));
    }
}