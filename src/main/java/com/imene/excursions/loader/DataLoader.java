package com.imene.excursions.loader;

import com.imene.excursions.model.Customer;
import com.imene.excursions.model.Excursion;

import java.io.IOException;
import java.util.List;

public interface DataLoader {

    List<Excursion> loadExcursions() throws IOException;

    List<Customer> loadCustomers() throws IOException;
}