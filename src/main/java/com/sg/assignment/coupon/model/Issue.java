package com.sg.assignment.coupon.model;

import java.util.List;
import lombok.Data;

@Data
public class Issue {

	private String id;

	private List<IssueCoupon> coupons;
}
