package com.sg.data.coupon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sg.data.coupon.model.Coupon;

public interface CouponRepository extends MongoRepository<Coupon, String> {

}
