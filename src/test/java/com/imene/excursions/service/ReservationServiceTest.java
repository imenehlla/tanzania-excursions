package com.imene.excursions.service;

import com.imene.excursions.exception.ConditionNotMetException;
import com.imene.excursions.exception.DateConflictException;
import com.imene.excursions.exception.ExcursionFullException;
import com.imene.excursions.exception.InvalidReservationException;
import com.imene.excursions.model.CityTour;
import com.imene.excursions.model.Customer;
import com.imene.excursions.model.Excursion;
import com.imene.excursions.model.Level;
import com.imene.excursions.model.Reservation;
import com.imene.excursions.model.Safari;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 10);
    private static final LocalDate EXCURSION_DATE = LocalDate.of(2026, 11, 20);

    private Excursion cityTour;
    private Excursion safari;
    private Excursion pastTour;
    private Excursion soonTour;

    private Customer imene;
    private Customer marie;
    private Customer paola;
    private Customer nelly;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        cityTour = new CityTour("EXC01", "Visite d'Arusha", EXCURSION_DATE, 40.0, 1, 2);
        safari = new Safari("EXC02", "Safari à Tarangire", EXCURSION_DATE, 150.0, 2, 6, 60.0);
        pastTour = new CityTour("EXC03", "Visite de Moshi", LocalDate.of(2026, 11, 1), 35.0, 1, 15);
        soonTour = new CityTour("EXC04", "Visite de Moshi", TODAY.plusDays(1), 35.0, 3, 15);

        imene = new Customer("CUS01", "Imène", LocalDate.of(2003, 10, 4), Level.INTERMEDIATE);
        marie = new Customer("CUS02", "Marie", LocalDate.of(2016, 3, 12), Level.BEGINNER);
        paola = new Customer("CUS03", "Paola", LocalDate.of(2021, 5, 20), Level.BEGINNER);
        nelly = new Customer("CUS04", "Nelly", LocalDate.of(1998, 7, 8), Level.ADVANCED);

        ExcursionService excursionService = new ExcursionService(List.of(cityTour, safari, pastTour, soonTour));
        reservationService = new ReservationService(excursionService);
    }

    @Test
    void shouldBookWhenAllRulesAreRespected() throws Exception {
        Reservation reservation = reservationService.book(imene, cityTour, TODAY);

        assertTrue(reservation.isConfirmed());
        assertEquals(1, cityTour.getParticipantCount());
    }

    @Test
    void shouldGenerateSuccessiveIds() throws Exception {
        Reservation first = reservationService.book(imene, cityTour, TODAY);
        Reservation second = reservationService.book(marie, cityTour, TODAY);

        assertEquals("RES001", first.getId());
        assertEquals("RES002", second.getId());
    }

    @Test
    void shouldRefuseWhenExcursionIsFull() throws Exception {
        reservationService.book(imene, cityTour, TODAY);
        reservationService.book(marie, cityTour, TODAY);

        assertThrows(ExcursionFullException.class, () -> reservationService.book(nelly, cityTour, TODAY));
        assertEquals(2, cityTour.getParticipantCount());
    }

    @Test
    void shouldRefuseWhenExcursionIsPast() {
        assertThrows(InvalidReservationException.class, () -> reservationService.book(imene, pastTour, TODAY));
    }

    @Test
    void shouldRefuseWhenConditionIsNotMet() {
        assertThrows(ConditionNotMetException.class, () -> reservationService.book(paola, safari, TODAY));
        assertEquals(0, safari.getParticipantCount());
    }

    @Test
    void shouldRefuseSecondExcursionOnSameDay() throws Exception {
        reservationService.book(imene, cityTour, TODAY);

        assertThrows(DateConflictException.class, () -> reservationService.book(imene, safari, TODAY));
    }

    @Test
    void shouldAllowSameDayAfterCancellation() throws Exception {
        Reservation reservation = reservationService.book(imene, cityTour, TODAY);
        reservationService.cancel(reservation.getId(), TODAY);

        assertDoesNotThrow(() -> reservationService.book(imene, safari, TODAY));
    }

    @Test
    void shouldCancelReservationAndFreePlace() throws Exception {
        Reservation reservation = reservationService.book(imene, cityTour, TODAY);

        reservationService.cancel(reservation.getId(), TODAY);

        assertFalse(reservation.isConfirmed());
        assertEquals(0, cityTour.getParticipantCount());
    }

    @Test
    void shouldRefuseCancellingUnknownReservation() {
        InvalidReservationException exception = assertThrows(InvalidReservationException.class,
                () -> reservationService.cancel("RES999", TODAY));

        assertEquals("Réservation introuvable : RES999", exception.getMessage());
    }

    @Test
    void shouldRefuseCancellingTwice() throws Exception {
        Reservation reservation = reservationService.book(imene, cityTour, TODAY);
        reservationService.cancel(reservation.getId(), TODAY);

        assertThrows(InvalidReservationException.class,
                () -> reservationService.cancel(reservation.getId(), TODAY));
    }

    @Test
    void shouldCancelSoonExcursionWithoutEnoughParticipants() throws Exception {
        Reservation reservation = reservationService.book(imene, soonTour, TODAY);

        List<Excursion> cancelled = reservationService.cancelExcursionsWithoutEnoughParticipants(TODAY);

        assertTrue(cancelled.contains(soonTour));
        assertTrue(soonTour.isCancelled());
        assertFalse(reservation.isConfirmed());
    }

    @Test
    void shouldNotCancelExcursionFarInTheFuture() {
        reservationService.cancelExcursionsWithoutEnoughParticipants(TODAY);

        assertFalse(cityTour.isCancelled());
    }
}