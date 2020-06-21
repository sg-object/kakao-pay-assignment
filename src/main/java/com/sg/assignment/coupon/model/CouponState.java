package com.sg.assignment.coupon.model;

import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ApiModel(description = "발급 쿠폰 상태(정보)")
@Data
public class CouponState {

	@ApiModelProperty(value = "쿠폰")
	private String coupon;

	@ApiModelProperty(value = "사용자 아이디")
	private String userId;

	@ApiModelProperty(value = "쿠폰 생성 날짜")
	private LocalDateTime createDate;

	@ApiModelProperty(value = "쿠폰 발급 날짜")
	private LocalDateTime issueDate;

	@ApiModelProperty(value = "쿠폰 만료 날짜")
	private LocalDateTime expireDate;

	public CouponState(IssueCoupon issueCoupon) {
		this.coupon = issueCoupon.getCoupon();
		this.userId = issueCoupon.getUserId();
		this.createDate = issueCoupon.getCreateDate();
		this.issueDate = issueCoupon.getIssueDate();
		this.expireDate = issueCoupon.getExpireDate();
	}
}
