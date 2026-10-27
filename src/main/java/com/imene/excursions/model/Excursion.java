package com.imene.excursions.model;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Excursion {

    private final String id;
    private final String name;
    private final LocalDate date;
    private final double basePrice;
    private final int minParticipants;
    private final int maxParticipants;

    private int participantCount;
    private boolean cancelled;

    protected Excursion(String id, String name, LocalDate date, double basePrice,
                        int minParticipants, int maxParticipants) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'identifiant de l'excursion ne peut pas être vide");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'excursion ne peut pas être vide");
        }
        if (date == null) {
            throw new IllegalArgumentException("La date de l'excursion est obligatoire");
        }
        if (basePrice < 0) {
            throw new IllegalArgumentException("Le prix de base ne peut pas être négatif : " + basePrice);
        }
        if (minParticipants < 1) {
            throw new IllegalArgumentException("Le minimum de participants doit être au moins 1 : " + minParticipants);
        }
        if (maxParticipants < minParticipants) {
            throw new IllegalArgumentException("Le maximum de participants (" + maxParticipants
                    + ") doit être supérieur ou égal au minimum (" + minParticipants + ")");
        }

        this.id = id;
        this.name = name;
        this.date = date;
        this.basePrice = basePrice;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.participantCount = 0;
        this.cancelled = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public int getMinParticipants() {
        return minParticipants;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isFull() {
        return participantCount >= maxParticipants;
    }

    public boolean hasEnoughParticipants() {
        return participantCount >= minParticipants;
    }

    public boolean isPast(LocalDate today) {
        return date.isBefore(today);
    }

    public void addParticipant() {
        if (cancelled) {
            throw new IllegalStateException("Impossible d'ajouter un participant : l'excursion " + id + " est annulée");
        }
        if (isFull()) {
            throw new IllegalStateException("Impossible d'ajouter un participant : l'excursion " + id + " est complète");
        }
        participantCount++;
    }

    public void removeParticipant() {
        if (participantCount == 0) {
            throw new IllegalStateException("Aucun participant à retirer pour l'excursion " + id);
        }
        participantCount--;
    }

    public void cancel() {
        cancelled = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Excursion other)) {
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
        return name + " (" + date + ")";
    }
}