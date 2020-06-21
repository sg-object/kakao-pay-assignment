package com.sg.assignment.coupon.model;

import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document("issues")
@Data
public class Issue {

	private String id;

	private List<IssueCoupon> coupons;
}
