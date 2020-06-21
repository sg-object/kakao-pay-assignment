package com.sg.data.coupon.controller;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sg.data.coupon.model.CouponState;
import com.sg.data.coupon.model.IssueCoupon;
import com.sg.data.coupon.service.CouponService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Api(description = "쿠폰 (발급, 조회, 사용, 취소)")
@RestController
@RequestMapping("/coupons")
public class CouponController {

	@Autowired
	private CouponService couponService;

	@ApiOperation(value = "쿠폰 발급", notes = "사용자 정보로 쿠폰 발급")
	@PostMapping("/issue")
	public String issueCoupon(@ApiIgnore Authentication authentication) {
		return couponService.issueCoupon(getLoginId(authentication));
	}

	@ApiOperation(value = "발급 쿠폰 조회", notes = "사용자 정보로 발급된 쿠폰 목록 조회", response = IssueCoupon.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", required = true, paramType = "query", value = "page or offset", defaultValue = "1"),
		@ApiImplicitParam(name = "size", required = true, paramType = "query", value = "size", defaultValue = "10")
	})
	@GetMapping
	public List<IssueCoupon> getMyCouponList(@ApiIgnore Authentication authentication, int page, int size) {
		return couponService.getMyCouponList(getLoginId(authentication), page, size);
	}

	@ApiOperation(value = "쿠폰 상세 조회", notes = "쿠폰 정보와 발급 날짜로 해당 쿠폰 정보 조회. Date Format : yyyy-MM-dd")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "coupon", required = true, paramType = "query", value = "쿠폰"),
		@ApiImplicitParam(name = "issueDate", required = true, paramType = "query", value = "발급 날짜(yyyy-MM-dd)", defaultValue = "2020-12-31")
	})
	@GetMapping("/state")
	public CouponState getCouponState(@ApiIgnore Authentication authentication, String coupon,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date issueDate) {
		return couponService.getCouponState(getLoginId(authentication), coupon, issueDate);
	}

	@ApiOperation(value = "쿠폰 사용", notes = "사용자 ID와 쿠폰 정보로 쿠폰 조회 및 사용 (만료 시 만료 처리)")
	@PutMapping("/use")
	public void useCoupon(@ApiIgnore Authentication authentication, @RequestBody IssueCoupon coupon) {
		couponService.useCoupon(getLoginId(authentication), coupon);
	}

	@ApiOperation(value = "쿠폰 사용 취소", notes = "사용자 ID와 쿠폰 정보로 쿠폰 조회 및 취소 처리 (만료 시 만료 처리)")
	@PutMapping("/cancellation")
	public void cancelCoupon(@ApiIgnore Authentication authentication, @RequestBody IssueCoupon coupon) {
		couponService.cancelCoupon(getLoginId(authentication), coupon);
	}

	private String getLoginId(Authentication authentication) {
		return authentication.getPrincipal().toString();
	}
}
