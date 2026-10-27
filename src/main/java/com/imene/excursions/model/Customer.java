package com.imene.excursions.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class Customer {

    private final String id;
    private final String name;
    private final LocalDate birthDate;
    private final Level level;

    public Customer(String id, String name, LocalDate birthDate, Level level) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'identifiant du client ne peut pas être vide");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom du client ne peut pas être vide");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("La date de naissance du client est obligatoire");
        }
        if (level == null) {
            throw new IllegalArgumentException("Le niveau sportif du client est obligatoire");
        }

        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Level getLevel() {
        return level;
    }

    public int getAgeOn(LocalDate date) {
        return Period.between(birthDate, date).getYears();
    }

    public boolean hasAtLeastLevel(Level requiredLevel) {
        return level.compareTo(requiredLevel) >= 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Customer other)) {
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
        return name + " (" + id + ")";
    }
}