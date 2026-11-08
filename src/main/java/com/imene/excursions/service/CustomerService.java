package com.imene.excursions.service;

import com.imene.excursions.model.Customer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomerService {

    private final Map<String, Customer> customersById = new LinkedHashMap<>();

    public CustomerService(List<Customer> customers) {
        for (Customer customer : customers) {
            customersById.put(customer.getId(), customer);
        }
    }

    public Optional<Customer> findById(String id) {
        return Optional.ofNullable(customersById.get(id));
    }

    public List<Customer> getAllCustomers() {
        return List.copyOf(customersById.values());
    }
}