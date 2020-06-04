package com.sg.assignment.coupon.model;

import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "발급 쿠폰")
@NoArgsConstructor
@Data
public class IssueCoupon {

	@ApiModelProperty(value = "쿠폰")
	private String coupon;

	@ApiModelProperty(hidden = true)
	private String collection;

	@ApiModelProperty(hidden = true)
	private LocalDateTime issueDate;

	@ApiModelProperty(hidden = true)
	private LocalDateTime expireDate;

	@ApiModelProperty(hidden = true)
	private boolean expireYn;

	@ApiModelProperty(hidden = true)
	private boolean useYn;

	public IssueCoupon(Coupon coupon) {
		this.coupon = coupon.getId();
		this.expireDate = coupon.getExpireDate();
	}
}
