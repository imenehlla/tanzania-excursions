package com.imene.excursions;

import com.imene.excursions.loader.CsvDataLoader;
import com.imene.excursions.loader.DataLoader;
import com.imene.excursions.model.Customer;
import com.imene.excursions.model.Excursion;
import com.imene.excursions.service.CustomerService;
import com.imene.excursions.service.ExcursionService;
import com.imene.excursions.service.ReservationService;
import com.imene.excursions.ui.ConsoleMenu;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Application {

    private static final Path EXCURSIONS_FILE = Path.of("src/main/resources/excursions.csv");
    private static final Path CUSTOMERS_FILE = Path.of("src/main/resources/customers.csv");

    public static void main(String[] args) {
        DataLoader dataLoader = new CsvDataLoader(EXCURSIONS_FILE, CUSTOMERS_FILE);

        List<Excursion> excursions;
        List<Customer> customers;
        try {
            excursions = dataLoader.loadExcursions();
            customers = dataLoader.loadCustomers();
        } catch (IOException e) {
            System.out.println("Fichier de données introuvable ou illisible : " + e.getMessage());
            return;
        }

        ExcursionService excursionService = new ExcursionService(excursions);
        CustomerService customerService = new CustomerService(customers);
        ReservationService reservationService = new ReservationService(excursionService);

        try (Scanner scanner = new Scanner(System.in)) {
            ConsoleMenu menu = new ConsoleMenu(excursionService, customerService, reservationService,
                    scanner, LocalDate.now());
            menu.start();
        }
    }
}