package com.nus.oribital.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nus.oribital.modal.Budget;

public interface BudgetRepository extends MongoRepository<Budget, String> {
    // custom query methods if needed
    List<Budget> findByUsername(String username);
}