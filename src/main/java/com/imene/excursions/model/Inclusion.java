package com.imene.excursions.model;

public enum Inclusion {
    GUIDE("guide"),
    LUNCH_BOX("lunch box"),
    SAFARI_VEHICLE("véhicule 4x4"),
    PARK_FEES("droits d'entrée du parc"),
    PORTERS("porteurs"),
    MARKET_VISIT("visite des marchés locaux"),
    MUSEUM_ENTRY("entrée du musée"),
    TRADITIONAL_SNACKS_TASTING("dégustation de snacks traditionnels");

    private final String label;

    Inclusion(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}