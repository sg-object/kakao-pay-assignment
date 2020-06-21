package com.sg.data.coupon.model;

import java.time.LocalDateTime;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Coupon {

	private String id;

	@ApiModelProperty(hidden = true)
	private LocalDateTime createDate;

	@ApiModelProperty(hidden = true)
	private LocalDateTime expireDate;

	@ApiModelProperty(hidden = true)
	private boolean issueYn;
}
