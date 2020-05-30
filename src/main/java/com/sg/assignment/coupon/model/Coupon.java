package com.sg.assignment.coupon.model;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document("coupons")
@Data
public class Coupon {

	private String coupon;
}
