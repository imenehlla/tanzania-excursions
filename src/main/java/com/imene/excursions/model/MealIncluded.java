package com.imene.excursions.model;

// Pas dans Excursion car la visite de ville n'a pas de repas
public interface MealIncluded {

    double LUNCH_BOX_PRICE = 8.0;

    default double getMealPrice() {
        return LUNCH_BOX_PRICE;
    }
}
