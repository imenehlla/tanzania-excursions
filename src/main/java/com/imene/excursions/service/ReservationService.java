package com.imene.excursions.service;

import com.imene.excursions.exception.ConditionNotMetException;
import com.imene.excursions.exception.DateConflictException;
import com.imene.excursions.exception.ExcursionFullException;
import com.imene.excursions.exception.InvalidReservationException;
import com.imene.excursions.model.Customer;
import com.imene.excursions.model.Excursion;
import com.imene.excursions.model.Reservation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationService {

    // L'agence doit savoir 2 jours avant si l'excursion a bien lieu
    private static final int MIN_PARTICIPANTS_CHECK_DAYS = 2;

    private final ExcursionService excursionService;
    private final List<Reservation> reservations = new ArrayList<>();
    private int nextReservationNumber = 1;

    public ReservationService(ExcursionService excursionService) {
        this.excursionService = excursionService;
    }

    public Reservation book(Customer customer, Excursion excursion, LocalDate today)
            throws InvalidReservationException, ExcursionFullException,
            ConditionNotMetException, DateConflictException {
        if (excursion.isCancelled()) {
            throw new InvalidReservationException("L'excursion " + excursion.getName() + " est annulée");
        }
        if (excursion.isPast(today)) {
            throw new InvalidReservationException("L'excursion " + excursion.getName() + " est déjà passée");
        }
        if (excursion.isFull()) {
            throw new ExcursionFullException("L'excursion " + excursion.getName() + " est complète");
        }
        excursion.checkEligibility(customer);
        if (hasReservationOnDate(customer, excursion.getDate())) {
            throw new DateConflictException(customer.getName() + " a déjà une excursion le " + excursion.getDate());
        }

        Reservation reservation = new Reservation(generateId(), customer, excursion, today);
        excursion.addParticipant();
        reservations.add(reservation);
        return reservation;
    }

    public void cancel(String reservationId, LocalDate today) throws InvalidReservationException {
        Reservation reservation = findById(reservationId)
                .orElseThrow(() -> new InvalidReservationException("Réservation introuvable : " + reservationId));
        if (!reservation.isConfirmed()) {
            throw new InvalidReservationException("La réservation " + reservationId + " est déjà annulée");
        }
        if (reservation.getExcursion().isPast(today)) {
            throw new InvalidReservationException("Impossible d'annuler : l'excursion est déjà passée");
        }

        reservation.cancel();
        reservation.getExcursion().removeParticipant();
    }

    public List<Excursion> cancelExcursionsWithoutEnoughParticipants(LocalDate today) {
        LocalDate limitDate = today.plusDays(MIN_PARTICIPANTS_CHECK_DAYS);

        List<Excursion> toCancel = excursionService.getUpcomingExcursions(today).stream()
                .filter(excursion -> !excursion.getDate().isAfter(limitDate))
                .filter(excursion -> !excursion.hasEnoughParticipants())
                .toList();

        for (Excursion excursion : toCancel) {
            excursion.cancel();
            for (Reservation reservation : getConfirmedReservations(excursion)) {
                reservation.cancel();
            }
        }
        return toCancel;
    }

    public Optional<Reservation> findById(String id) {
        return reservations.stream()
                .filter(reservation -> reservation.getId().equals(id))
                .findFirst();
    }

    public List<Reservation> getReservationsOf(Customer customer) {
        return reservations.stream()
                .filter(reservation -> reservation.getCustomer().equals(customer))
                .toList();
    }

    private List<Reservation> getConfirmedReservations(Excursion excursion) {
        return reservations.stream()
                .filter(Reservation::isConfirmed)
                .filter(reservation -> reservation.getExcursion().equals(excursion))
                .toList();
    }

    private boolean hasReservationOnDate(Customer customer, LocalDate date) {
        return reservations.stream()
                .filter(Reservation::isConfirmed)
                .anyMatch(reservation -> reservation.getCustomer().equals(customer)
                        && reservation.getExcursion().getDate().equals(date));
    }

    private String generateId() {
        return String.format("RES%03d", nextReservationNumber++);
    }
}