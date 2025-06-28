package com.nus.oribital.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nus.oribital.modal.SharedBudget;

public interface SharedBudgetRepository extends MongoRepository<SharedBudget, String> {

}
