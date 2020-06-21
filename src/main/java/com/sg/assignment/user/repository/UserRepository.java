package com.sg.assignment.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.sg.assignment.user.model.User;

public interface UserRepository extends MongoRepository<User, String> {
}
