package com.nus.oribital.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nus.oribital.modal.User;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username); // Custom query method to find a user by username

}
