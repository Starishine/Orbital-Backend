package com.nus.oribital.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nus.oribital.modal.Greeting;

public interface GreetingRepository extends MongoRepository<Greeting, String> {
}