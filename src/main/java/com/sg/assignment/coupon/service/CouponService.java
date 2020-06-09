package com.sg.assignment.coupon.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mongodb.client.model.Filters;
import com.sg.assignment.common.enums.CouponField;
import com.sg.assignment.common.exception.InvalidCouponException;
import com.sg.assignment.common.exception.NotFoundOrExpiredCouponException;
import com.sg.assignment.common.exception.VerificationException;
import com.sg.assignment.common.service.MongoService;
import com.sg.assignment.common.util.VerificationUtils;
import com.sg.assignment.coupon.model.CouponState;
import com.sg.assignment.coupon.model.IssueCoupon;

@Service
public class CouponService {

	@Autowired
	private MongoService mongoService;

	@Autowired
	private IssueService issueService;

	public String issueCoupon(String id) {
		IssueCoupon issueCoupon = issueService.issueCoupon(id);
		issueService.issueCoupon(issueCoupon);
		return issueCoupon.getCoupon();
	}

	public List<IssueCoupon> getMyCouponList(String id, int page, int size) {
		if (page < 1 || size < 1) {
			throw new VerificationException();
		}
		return issueService.getMyCouponList(id, page, size);
	}

	public CouponState getCouponState(String id, String coupon, Date issueDate) {
		checkCoupon(coupon);
		String collection = mongoService.createCollectionNameByIssueDate(
				LocalDateTime.ofInstant(issueDate.toInstant(), ZoneId.systemDefault()));
		CouponState couponState = mongoService.getCollection(collection)
				.find(Filters.and(Filters.eq(CouponField.COUPON.getField(), coupon),
						Filters.eq(CouponField.USER_ID.getField(), id)), CouponState.class)
				.limit(1).first();
		if (couponState == null) {
			throw new NotFoundOrExpiredCouponException();
		}
		return couponState;
	}

	public void useCoupon(String id, IssueCoupon coupon) {
		checkCoupon(coupon.getCoupon());
		issueService.useCoupon(id, coupon.getCoupon());
	}

	public void cancelCoupon(String id, IssueCoupon coupon) {
		checkCoupon(coupon.getCoupon());
		issueService.cancelCoupon(id, coupon.getCoupon());
	}

	private void checkCoupon(String coupon) {
		if (VerificationUtils.isNullOrBlank(coupon)) {
			throw new InvalidCouponException();
		}
	}
}
