package com.imene.excursions.model;

import com.imene.excursions.exception.ConditionNotMetException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Hike extends Excursion implements MealIncluded {

    private final Level requiredLevel;

    public Hike(String id, String name, LocalDate date, double basePrice,
                int minParticipants, int maxParticipants, Level requiredLevel) {
        super(id, name, date, basePrice, minParticipants, maxParticipants);
        if (requiredLevel == null) {
            throw new IllegalArgumentException("Le niveau requis de la randonnée est obligatoire");
        }
        this.requiredLevel = requiredLevel;
    }

    public Level getRequiredLevel() {
        return requiredLevel;
    }

    // Une randonnée est difficile quand elle demande le niveau le plus élevé (ADVANCED)
    private boolean isDifficult() {
        return requiredLevel == Level.ADVANCED;
    }

    @Override
    public void checkEligibility(Customer customer) throws ConditionNotMetException {
        if (!customer.hasAtLeastLevel(requiredLevel)) {
            throw new ConditionNotMetException("Niveau insuffisant pour " + getName()
                    + " : niveau " + requiredLevel + " requis, le client a le niveau " + customer.getLevel());
        }
    }

    @Override
    public double calculatePrice(Customer customer) {
        return getBasePrice() + getMealPrice();
    }

    @Override
    public List<Inclusion> getInclusions() {
        // ArrayList car List.of ne permet pas d'ajouter les porteurs ensuite
        List<Inclusion> inclusions = new ArrayList<>(List.of(Inclusion.GUIDE, Inclusion.LUNCH_BOX));
        if (isDifficult()) {
            inclusions.add(Inclusion.PORTERS);
        }
        return inclusions;
    }
}