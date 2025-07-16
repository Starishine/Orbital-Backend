package com.nus.oribital.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nus.oribital.modal.SharedExpense;

public interface SharedExpenseRepository extends MongoRepository<SharedExpense, String> {

    List<SharedExpense> findAllByBudgetId(String budgetId);

}
