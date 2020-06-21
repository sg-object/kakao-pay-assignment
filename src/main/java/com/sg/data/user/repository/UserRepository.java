package com.sg.data.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sg.data.user.model.User;

public interface UserRepository extends MongoRepository<User, String> {
}
