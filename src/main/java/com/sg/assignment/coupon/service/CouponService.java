package com.sg.assignment.coupon.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoException;
import com.sg.assignment.common.enums.CouponField;
import com.sg.assignment.common.exception.InvalidCouponException;
import com.sg.assignment.common.exception.IssueCouponException;
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

	@Autowired
	private MongoTemplate mongoTemplate;

	public String issueCoupon(String id) {
		IssueCoupon issueCoupon = issueService.issueCoupon(id);
		try {
			issueService.issueCoupon(issueCoupon);
		}catch (UncategorizedMongoDbException e) {
			if(e.getRootCause() instanceof MongoCommandException) {
				if(((MongoCommandException) e.getRootCause()).hasErrorLabel(MongoException.TRANSIENT_TRANSACTION_ERROR_LABEL)) {
					throw new IssueCouponException();
				};
			}
			throw new RuntimeException();
		}
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
		String collectionName = mongoService.createCollectionNameByIssueDate(
				LocalDateTime.ofInstant(issueDate.toInstant(), ZoneId.systemDefault()));
		Query query = new Query();
		query.addCriteria(Criteria.where(CouponField.COUPON.getField()).is(coupon));
		query.addCriteria(Criteria.where(CouponField.USER_ID.getField()).is(id));
		mongoTemplate.findOne(query, CouponState.class, collectionName);
		return Optional.ofNullable(mongoTemplate.findOne(query, CouponState.class, collectionName))
				.orElseThrow(() -> new NotFoundOrExpiredCouponException());
	}

	public void useCoupon(String id, IssueCoupon coupon) {
		checkCoupon(coupon.getCoupon());
		if (!issueService.useCoupon(id, coupon.getCoupon())) {
			throw new NotFoundOrExpiredCouponException();
		}
	}

	public void cancelCoupon(String id, IssueCoupon coupon) {
		checkCoupon(coupon.getCoupon());
		if (!issueService.cancelCoupon(id, coupon.getCoupon())) {
			throw new NotFoundOrExpiredCouponException();
		}
	}

	private void checkCoupon(String coupon) {
		if (VerificationUtils.isNullOrBlank(coupon)) {
			throw new InvalidCouponException();
		}
	}
}
