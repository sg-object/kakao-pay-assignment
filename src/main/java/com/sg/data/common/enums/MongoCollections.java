package com.sg.data.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MongoCollections {

	USER("users"), COUPON("coupons"), ISSUE("issues"), EXPIRE_COUPON("expire_coupons");

	@Getter
	private String collectionName;
}
