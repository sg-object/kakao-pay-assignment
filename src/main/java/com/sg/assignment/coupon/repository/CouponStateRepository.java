package com.sg.assignment.coupon.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.sg.assignment.coupon.model.CouponState;

public interface CouponStateRepository extends MongoRepository<CouponState, ObjectId>{

}
