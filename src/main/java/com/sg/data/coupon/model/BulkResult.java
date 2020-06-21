package com.sg.data.coupon.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "쿠폰 생성 결과")
@Data
public class BulkResult {

	@ApiModelProperty(value = "총 요청 갯수")
	private int totalCount;

	@ApiModelProperty(value = "생성 갯수")
	private int createCount;

	@ApiModelProperty(value = "중복 갯수")
	private int duplicateCount;
}
