package com.sg.assignment.coupon.model;

import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "발급 쿠폰 상태(정보)")
@Data
public class CouponState {

	@ApiModelProperty(value = "쿠폰(MongoDB ID)")
	private String id;

	@ApiModelProperty(value = "사용자 아이디")
	private String userId;

	@ApiModelProperty(value = "쿠폰 생성 날짜")
	private LocalDateTime createDate;

	@ApiModelProperty(value = "쿠폰 발급 날짜")
	private LocalDateTime issueDate;

	@ApiModelProperty(value = "쿠폰 만료 날짜")
	private LocalDateTime expireDate;
}
