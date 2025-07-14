package com.nus.oribital.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nus.oribital.modal.SharedBudget;

public interface SharedBudgetRepository extends MongoRepository<SharedBudget, String> {

    @Query("{ 'invites.token': ?0 }")
    SharedBudget findByInviteeToken(String token);

}
