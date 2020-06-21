package com.sg.data.coupon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sg.data.coupon.model.Issue;

public interface IssueRepository extends MongoRepository<Issue, String> {

}
