package com.nus.oribital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nus.oribital.modal.SharedBudget;

public interface SharedBudgetRepository extends MongoRepository<SharedBudget, String> {

    @Query("{ 'invites.token': ?0 }")
    SharedBudget findByInviteeToken(String token);

    @Query("{ $or: [ { 'inviter': ?0 }, { 'invites.username': ?0 } ], 'budgetName': ?1 }")
    Optional<SharedBudget> findByUserAndBudgetName(String username, String budgetName);

    @Query("{ $or: [ { 'inviter': ?0 }, { 'invites.username': ?0 } ] }")
    List<SharedBudget> findAllByUser(String username);

}
