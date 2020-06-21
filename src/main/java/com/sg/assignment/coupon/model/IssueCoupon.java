package com.sg.assignment.coupon.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

	@JsonIgnore
	@Transient
	private LocalDateTime createDate;

	@JsonIgnore
	@Transient
	private String userId;

	@JsonIgnore
	@Transient
	private String expireCollection;

	public IssueCoupon(Coupon coupon) {
		this.coupon = coupon.getId();
		this.expireDate = coupon.getExpireDate();
		this.createDate = coupon.getCreateDate();
	}
}
