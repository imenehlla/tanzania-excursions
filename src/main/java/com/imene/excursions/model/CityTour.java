package com.imene.excursions.model;

import java.time.LocalDate;
import java.util.List;

public class CityTour extends Excursion {

    private static final int CHILD_AGE_LIMIT = 12;
    private static final double CHILD_DISCOUNT = 0.5;

    public CityTour(String id, String name, LocalDate date, double basePrice,
                    int minParticipants, int maxParticipants) {
        super(id, name, date, basePrice, minParticipants, maxParticipants);
    }

    // Visite ouverte à tous, donc aucune condition à vérifier
    @Override
    public void checkEligibility(Customer customer) {
    }

    @Override
    public double calculatePrice(Customer customer) {
        if (customer.getAgeOn(getDate()) < CHILD_AGE_LIMIT) {
            return getBasePrice() * CHILD_DISCOUNT;
        }
        return getBasePrice();
    }

    @Override
    public List<Inclusion> getInclusions() {
        return List.of(Inclusion.GUIDE, Inclusion.MARKET_VISIT, Inclusion.MUSEUM_ENTRY,
                Inclusion.TRADITIONAL_SNACKS_TASTING);
    }
}