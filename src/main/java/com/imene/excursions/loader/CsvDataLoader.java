package com.imene.excursions.loader;

import com.imene.excursions.model.CityTour;
import com.imene.excursions.model.Customer;
import com.imene.excursions.model.Excursion;
import com.imene.excursions.model.Hike;
import com.imene.excursions.model.Level;
import com.imene.excursions.model.Safari;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CsvDataLoader implements DataLoader {

    private static final String SEPARATOR = ",";
    private static final int EXCURSION_COLUMNS = 8;
    private static final int CUSTOMER_COLUMNS = 4;

    private final Path excursionsFile;
    private final Path customersFile;

    public CsvDataLoader(Path excursionsFile, Path customersFile) {
        this.excursionsFile = excursionsFile;
        this.customersFile = customersFile;
    }

    @Override
    public List<Excursion> loadExcursions() throws IOException {
        List<String> lines = Files.readAllLines(excursionsFile);
        List<Excursion> excursions = new ArrayList<>();

        // On commence à 1 car la première ligne contient les noms des colonnes
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                excursions.add(parseExcursion(line));
            } catch (IllegalArgumentException | DateTimeParseException e) {
                System.out.println("Ligne " + (i + 1) + " des excursions ignorée : " + e.getMessage());
            }
        }
        return excursions;
    }

    @Override
    public List<Customer> loadCustomers() throws IOException {
        List<String> lines = Files.readAllLines(customersFile);
        List<Customer> customers = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                customers.add(parseCustomer(line));
            } catch (IllegalArgumentException | DateTimeParseException e) {
                System.out.println("Ligne " + (i + 1) + " des clients ignorée : " + e.getMessage());
            }
        }
        return customers;
    }

    private Excursion parseExcursion(String line) {
        // -1 pour garder la dernière colonne même vide (cas des visites de ville)
        String[] columns = line.split(SEPARATOR, -1);
        if (columns.length != EXCURSION_COLUMNS) {
            throw new IllegalArgumentException(EXCURSION_COLUMNS + " colonnes attendues, " + columns.length + " trouvées");
        }

        String type = columns[0].trim();
        String id = columns[1].trim();
        String name = columns[2].trim();
        LocalDate date = LocalDate.parse(columns[3].trim());
        double basePrice = Double.parseDouble(columns[4].trim());
        int minParticipants = Integer.parseInt(columns[5].trim());
        int maxParticipants = Integer.parseInt(columns[6].trim());
        String extra = columns[7].trim();

        // Seul endroit où on regarde le type, pour créer le bon objet
        return switch (type) {
            case "HIKE" -> new Hike(id, name, date, basePrice, minParticipants, maxParticipants,
                    Level.valueOf(extra));
            case "SAFARI" -> new Safari(id, name, date, basePrice, minParticipants, maxParticipants,
                    Double.parseDouble(extra));
            case "CITY_TOUR" -> new CityTour(id, name, date, basePrice, minParticipants, maxParticipants);
            default -> throw new IllegalArgumentException("Type d'excursion inconnu : " + type);
        };
    }

    private Customer parseCustomer(String line) {
        String[] columns = line.split(SEPARATOR, -1);
        if (columns.length != CUSTOMER_COLUMNS) {
            throw new IllegalArgumentException(CUSTOMER_COLUMNS + " colonnes attendues, " + columns.length + " trouvées");
        }

        String id = columns[0].trim();
        String name = columns[1].trim();
        LocalDate birthDate = LocalDate.parse(columns[2].trim());
        Level level = Level.valueOf(columns[3].trim());

        return new Customer(id, name, birthDate, level);
    }
}