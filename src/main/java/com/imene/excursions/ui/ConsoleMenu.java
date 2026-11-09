package com.imene.excursions.ui;

import com.imene.excursions.exception.ConditionNotMetException;
import com.imene.excursions.exception.DateConflictException;
import com.imene.excursions.exception.ExcursionFullException;
import com.imene.excursions.exception.InvalidReservationException;
import com.imene.excursions.model.Customer;
import com.imene.excursions.model.Excursion;
import com.imene.excursions.model.Inclusion;
import com.imene.excursions.model.Reservation;
import com.imene.excursions.service.CustomerService;
import com.imene.excursions.service.ExcursionService;
import com.imene.excursions.service.ReservationService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ConsoleMenu {

    private final ExcursionService excursionService;
    private final CustomerService customerService;
    private final ReservationService reservationService;
    private final Scanner scanner;
    private final LocalDate today;

    public ConsoleMenu(ExcursionService excursionService, CustomerService customerService,
                       ReservationService reservationService, Scanner scanner, LocalDate today) {
        this.excursionService = excursionService;
        this.customerService = customerService;
        this.reservationService = reservationService;
        this.scanner = scanner;
        this.today = today;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> showUpcomingExcursions();
                case "2" -> showCustomers();
                case "3" -> bookExcursion();
                case "4" -> cancelReservation();
                case "5" -> showCustomerReservations();
                case "6" -> checkMinimumParticipants();
                case "0" -> running = false;
                default -> System.out.println("Choix invalide, veuillez réessayer.");
            }
        }
        System.out.println("Au revoir !");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Excursions guidées en Tanzanie ===");
        System.out.println("1. Voir les excursions à venir");
        System.out.println("2. Voir les clients");
        System.out.println("3. Réserver une excursion");
        System.out.println("4. Annuler une réservation");
        System.out.println("5. Voir les réservations d'un client");
        System.out.println("6. Vérifier le minimum de participants");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    private void showUpcomingExcursions() {
        List<Excursion> excursions = excursionService.getUpcomingExcursions(today);
        if (excursions.isEmpty()) {
            System.out.println("Aucune excursion à venir.");
            return;
        }
        for (Excursion excursion : excursions) {
            System.out.println(excursion.getId() + " - " + excursion.getName() + " - " + excursion.getDate()
                    + " - " + excursion.getParticipantCount() + "/" + excursion.getMaxParticipants() + " places réservées");
            System.out.println("    Inclus : " + formatInclusions(excursion.getInclusions()));
        }
    }

    private void showCustomers() {
        for (Customer customer : customerService.getAllCustomers()) {
            System.out.println(customer.getId() + " - " + customer.getName() + " - niveau " + customer.getLevel());
        }
    }

    private void bookExcursion() {
        Optional<Customer> customer = askCustomer();
        if (customer.isEmpty()) {
            return;
        }
        Optional<Excursion> excursion = askExcursion();
        if (excursion.isEmpty()) {
            return;
        }

        try {
            Reservation reservation = reservationService.book(customer.get(), excursion.get(), today);
            System.out.println("Réservation confirmée : " + reservation.getId());
            System.out.println("Prix : " + formatPrice(reservation.getPrice()));
            System.out.println("Inclus : " + formatInclusions(excursion.get().getInclusions()));
        } catch (ExcursionFullException | ConditionNotMetException
                 | DateConflictException | InvalidReservationException e) {
            System.out.println("Réservation impossible : " + e.getMessage());
        }
    }

    private void cancelReservation() {
        System.out.print("Identifiant de la réservation : ");
        String id = scanner.nextLine().trim().toUpperCase();
        try {
            reservationService.cancel(id, today);
            System.out.println("Réservation " + id + " annulée.");
        } catch (InvalidReservationException e) {
            System.out.println("Annulation impossible : " + e.getMessage());
        }
    }

    private void showCustomerReservations() {
        Optional<Customer> customer = askCustomer();
        if (customer.isEmpty()) {
            return;
        }

        List<Reservation> reservations = reservationService.getReservationsOf(customer.get());
        if (reservations.isEmpty()) {
            System.out.println("Aucune réservation pour " + customer.get().getName() + ".");
            return;
        }
        for (Reservation reservation : reservations) {
            String status = reservation.isConfirmed() ? "confirmée" : "annulée";
            System.out.println(reservation.getId() + " - " + reservation.getExcursion().getName()
                    + " - " + reservation.getExcursion().getDate()
                    + " - " + formatPrice(reservation.getPrice()) + " - " + status);
        }
    }

    private void checkMinimumParticipants() {
        List<Excursion> cancelled = reservationService.cancelExcursionsWithoutEnoughParticipants(today);
        if (cancelled.isEmpty()) {
            System.out.println("Aucune excursion à annuler dans les 2 prochains jours.");
            return;
        }
        for (Excursion excursion : cancelled) {
            System.out.println("Excursion annulée : " + excursion.getName() + " (" + excursion.getDate()
                    + "), " + excursion.getParticipantCount() + "/" + excursion.getMinParticipants()
                    + " participants minimum");
        }
    }

    private Optional<Customer> askCustomer() {
        System.out.print("Identifiant du client : ");
        // En majuscules pour accepter "cus01" comme "CUS01"
        String id = scanner.nextLine().trim().toUpperCase();
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isEmpty()) {
            System.out.println("Client introuvable : " + id);
        }
        return customer;
    }

    private Optional<Excursion> askExcursion() {
        System.out.print("Identifiant de l'excursion : ");
        String id = scanner.nextLine().trim().toUpperCase();
        Optional<Excursion> excursion = excursionService.findById(id);
        if (excursion.isEmpty()) {
            System.out.println("Excursion introuvable : " + id);
        }
        return excursion;
    }

    private String formatInclusions(List<Inclusion> inclusions) {
        return inclusions.stream()
                .map(Inclusion::getLabel)
                .collect(Collectors.joining(", "));
    }

    private String formatPrice(double price) {
        return String.format("%.2f $", price);
    }
}