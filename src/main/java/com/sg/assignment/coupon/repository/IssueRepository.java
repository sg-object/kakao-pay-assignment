package com.sg.assignment.coupon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.sg.assignment.coupon.model.Issue;

public interface IssueRepository extends MongoRepository<Issue, String> {

}
