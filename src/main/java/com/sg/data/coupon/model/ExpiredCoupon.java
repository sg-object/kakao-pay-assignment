package com.sg.data.coupon.model;

import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ApiModel(description = "만료 쿠폰 정보")
@Data
public class ExpiredCoupon {

	@ApiModelProperty(value = "쿠폰")
	private String coupon;

	@ApiModelProperty(value = "사용자 아이디")
	private String userId;

	@ApiModelProperty(value = "쿠폰 저장 Collection(MongoDB)")
	private String collection;

	@ApiModelProperty(value = "발급 날짜")
	private LocalDateTime issueDate;

	@ApiModelProperty(value = "만료 날짜")
	private LocalDateTime expireDate;

	public ExpiredCoupon(IssueCoupon issueCoupon) {
		this.coupon = issueCoupon.getCoupon();
		this.userId = issueCoupon.getUserId();
		this.collection = issueCoupon.getCollection();
		this.issueDate = issueCoupon.getIssueDate();
		this.expireDate = issueCoupon.getExpireDate();
	}
}
