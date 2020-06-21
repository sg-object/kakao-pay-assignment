package com.sg.assignment.coupon.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.sg.assignment.coupon.model.BulkResult;
import com.sg.assignment.coupon.model.ExpiredCoupon;
import com.sg.assignment.coupon.service.ManagementCouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(description = "쿠폰 관리(생성, 조회)")
@RestController
@RequestMapping("/management/coupons")
public class ManagementCouponController {

	@Autowired
	private ManagementCouponService managementCouponService;

	@ApiOperation(value = "쿠폰 생성 (API)", notes = "Parameter 값 만큼 생성, 쿠폰 발급 시 Expire Date는 설정하지 않지만 테스트를 위해 Parameter값으로 설정 가능. Date Format : yyyyMMddHH")
	@PostMapping("/bulk")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "count", required = true, paramType = "query", value = "발급 갯수", defaultValue = "1000"),
		@ApiImplicitParam(name = "expireDate", required = false, paramType = "query", value = "만료 날짜(yyyyMMddHH)", defaultValue = "2020123123")
	})
	public BulkResult createBulkCoupon(int count, @DateTimeFormat(pattern = "yyyyMMddHH") Date expireDate) {
		return managementCouponService.createBulkCoupon(count, expireDate);
	}

	@ApiOperation(value = "쿠폰 생성 (CSV)", notes = "CSV 파일을 이용한 쿠폰 생성")
	@PostMapping("/bulk/csv")
	public BulkResult createBulkCouponByCSV(@RequestParam("csv") MultipartFile csv) {
		return managementCouponService.createBulkCouponByCSV(csv);
	}

	@ApiOperation(value = "당일 만료 쿠폰 조회", notes = "당일 만료된 쿠폰 조회. 당일 만료 쿠폰이어도 만료 시간이 지나지 않으면 검색되지 않음. (만료 날짜 설정 시 시간까지만 설정)", response = ExpiredCoupon.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", required = true, paramType = "query", value = "page or offset", defaultValue = "1"),
		@ApiImplicitParam(name = "size", required = true, paramType = "query", value = "size", defaultValue = "10")
	})
	@GetMapping("/expiration")
	public List<ExpiredCoupon> getExpiredCouponAtToday(int page, int size) {
		return managementCouponService.getExpiredCouponAtDate(page, size, LocalDateTime.now());
	}
}
