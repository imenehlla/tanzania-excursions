package com.imene.excursions.model;

import com.imene.excursions.exception.ConditionNotMetException;

import java.time.LocalDate;
import java.util.List;

public class Safari extends Excursion implements MealIncluded {

    //    // Âge minimum pour des raisons de sécurité dans le véhicule
    private static final int MINIMUM_AGE = 6;

    private final double parkFees;

    public Safari(String id, String name, LocalDate date, double basePrice,
                  int minParticipants, int maxParticipants, double parkFees) {
        super(id, name, date, basePrice, minParticipants, maxParticipants);
        if (parkFees < 0) {
            throw new IllegalArgumentException("Les droits d'entrée du parc ne peuvent pas être négatifs : " + parkFees);
        }
        this.parkFees = parkFees;
    }

    public double getParkFees() {
        return parkFees;
    }

    @Override
    public void checkEligibility(Customer customer) throws ConditionNotMetException {
        int age = customer.getAgeOn(getDate());
        if (age < MINIMUM_AGE) {
            throw new ConditionNotMetException("Âge insuffisant pour " + getName()
                    + " : " + MINIMUM_AGE + " ans minimum, le client aura " + age + " ans");
        }
    }

    @Override
    public double calculatePrice(Customer customer) {
        return getBasePrice() + parkFees + getMealPrice();
    }

    @Override
    public List<Inclusion> getInclusions() {
        return List.of(Inclusion.GUIDE, Inclusion.SAFARI_VEHICLE, Inclusion.PARK_FEES, Inclusion.LUNCH_BOX);
    }
}