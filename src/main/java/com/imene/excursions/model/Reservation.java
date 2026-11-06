package com.imene.excursions.model;

import java.time.LocalDate;
import java.util.Objects;

public class Reservation {

    private final String id;
    private final Customer customer;
    private final Excursion excursion;
    private final LocalDate reservationDate;
    private final double price;

    private ReservationStatus status;

    public Reservation(String id, Customer customer, Excursion excursion, LocalDate reservationDate) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'identifiant de la réservation ne peut pas être vide");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Le client de la réservation est obligatoire");
        }
        if (excursion == null) {
            throw new IllegalArgumentException("L'excursion de la réservation est obligatoire");
        }
        if (reservationDate == null) {
            throw new IllegalArgumentException("La date de réservation est obligatoire");
        }

        this.id = id;
        this.customer = customer;
        this.excursion = excursion;
        this.reservationDate = reservationDate;
        // Le prix est fixé au moment de la réservation et ne change plus ensuite
        this.price = excursion.calculatePrice(customer);
        this.status = ReservationStatus.CONFIRMED;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Excursion getExcursion() {
        return excursion;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public double getPrice() {
        return price;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public boolean isConfirmed() {
        return status == ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        if (!isConfirmed()) {
            throw new IllegalStateException("La réservation " + id + " est déjà annulée");
        }
        status = ReservationStatus.CANCELLED;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Reservation other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " : " + customer.getName() + " - " + excursion.getName() + " (" + price + ")";
    }
}