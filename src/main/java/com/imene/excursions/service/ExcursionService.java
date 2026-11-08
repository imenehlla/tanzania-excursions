package com.imene.excursions.service;

import com.imene.excursions.model.Excursion;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExcursionService {

    private final Map<String, Excursion> excursionsById = new LinkedHashMap<>();

    public ExcursionService(List<Excursion> excursions) {
        for (Excursion excursion : excursions) {
            excursionsById.put(excursion.getId(), excursion);
        }
    }

    public Optional<Excursion> findById(String id) {
        return Optional.ofNullable(excursionsById.get(id));
    }

    public List<Excursion> getUpcomingExcursions(LocalDate today) {
        return excursionsById.values().stream()
                .filter(excursion -> !excursion.isPast(today))
                .filter(excursion -> !excursion.isCancelled())
                .sorted(Comparator.comparing(Excursion::getDate))
                .toList();
    }
}