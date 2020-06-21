package com.sg.assignment.coupon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.sg.assignment.coupon.model.Coupon;

public interface CouponRepository extends MongoRepository<Coupon, String> {

}
