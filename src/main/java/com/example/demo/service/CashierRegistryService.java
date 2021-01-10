package com.example.demo.service;

import com.example.demo.model.CashierRegistry;

public interface CashierRegistryService {

    CashierRegistry setUpCashierRegistry(Long id);

    String showCashierRegistryState(CashierRegistry registry);

    String putBillsForEachDenomination(CashierRegistry registry, String input);

    String takeBillsForEachDenomination(CashierRegistry registry, String input);

    String getChangeForValue(CashierRegistry registry, String value);

}
