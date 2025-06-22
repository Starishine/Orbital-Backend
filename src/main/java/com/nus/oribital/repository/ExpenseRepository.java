package com.nus.oribital.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nus.oribital.modal.Expense;

public interface ExpenseRepository extends MongoRepository<Expense, String> {

    List<Expense> findByUsername(String username);
}
