package com.sg.assignment.coupon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.sg.assignment.coupon.service.CouponService;
import io.swagger.annotations.ApiImplicitParam;

@RestController
@RequestMapping("/coupons")
public class CouponController {

	@Autowired
	private CouponService couponService;

	@PostMapping
	@ApiImplicitParam(name = "count", required = true, paramType = "query")
	public void createBulkCoupon(int count) {
		couponService.createBulkCoupon(count);
	}

	@PostMapping("/csv")
	public void createCouponByCSV(@RequestParam("csv") MultipartFile csv) {
		couponService.createCouponByCSV(csv);
	}
}
