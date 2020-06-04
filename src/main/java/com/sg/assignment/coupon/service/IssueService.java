package com.sg.assignment.coupon.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.sg.assignment.common.enums.CommonField;
import com.sg.assignment.common.enums.CouponField;
import com.sg.assignment.common.exception.CanceledCouponException;
import com.sg.assignment.common.exception.NotFoundOrExpiredCouponException;
import com.sg.assignment.common.exception.NotFoundUsableCouponException;
import com.sg.assignment.common.exception.UsedCouponException;
import com.sg.assignment.common.enums.IssueField;
import com.sg.assignment.common.service.MongoService;
import com.sg.assignment.coupon.model.Coupon;
import com.sg.assignment.coupon.model.Issue;
import com.sg.assignment.coupon.model.IssueCoupon;

@Service
public class IssueService {

	@Value("${coupon.default-expire-day}")
	private int defaultExpireDay;
	
	@Autowired
	private MongoService mongoService;

	public String issueCoupon(String id) {
		LocalDateTime issueDate = LocalDateTime.now();
		MongoCollection<Document> issueCollection = mongoService.getIssueCollection();

		AggregateIterable<Document> issuedCoupons = issueCollection
				.aggregate(Arrays.asList(Aggregates.match(Filters.eq(CommonField._ID.getField(), id)),
						Aggregates.unwind("$" + IssueField.COUPONS.getField()),
						Aggregates.match(Filters.gte(IssueField.COUPONS_EXPIRE_DATE.getField(), LocalDate.now())),
						Aggregates.group("$" + IssueField.COUPONS_COUPON.getField())));

		List<String> notIn = new ArrayList<String>();
		if(issuedCoupons != null) {
			issuedCoupons.forEach(coupon -> {
				notIn.add(coupon.get(CommonField._ID.getField()).toString());
			});	
		}

		Coupon coupon = getUsableCoupon(notIn);
		IssueCoupon issueCoupon = new IssueCoupon(coupon);
		issueCoupon.setUseYn(false);
		issueCoupon.setExpireYn(false);
		issueCoupon.setCollection(mongoService.createCollectionNameByIssueDate(issueDate));
		issueCoupon.setIssueDate(issueDate);

		issueCollection
				.updateOne(Filters.eq(CommonField._ID.getField(), id),
						new Document(CommonField.ADD_TO_SET.getField(),
								new Document(IssueField.COUPONS.getField(), issueCoupon)),
						new UpdateOptions().upsert(true));

		// 쿠폰 관리 Collection
		mongoService.getCollection(issueCoupon.getCollection()).insertOne(createStateDocument(coupon, id, issueDate));
		// 쿠폰 만료 관리 Collection
		mongoService.getCollection(mongoService.createCollectionNameByExpireDate(issueCoupon.getExpireDate())).insertOne(createExpireDocument(issueCoupon, id));
		return coupon.getId();
	}

	public List<IssueCoupon> getMyCouponList(String id, int page, int size) {
		MongoCollection<Document> issueCollection = mongoService.getIssueCollection();
		AggregateIterable<IssueCoupon> issuedCoupons = issueCollection
				.aggregate(Arrays.asList(Aggregates.match(Filters.eq(CommonField._ID.getField(), id)),
						Aggregates.unwind("$" + IssueField.COUPONS.getField()),
						Aggregates.sort(new Document(IssueField.COUPONS_ISSUE_DATE.getField(), -1)),
						Aggregates.skip((page-1) * size),
						Aggregates.limit(size),
						Aggregates.replaceRoot("$" + IssueField.COUPONS.getField())), IssueCoupon.class);
		
		List<IssueCoupon> result = new ArrayList<IssueCoupon>();
		if(issuedCoupons != null) {
			issuedCoupons.forEach(coupon -> {
				result.add(coupon);
			});
		}
		return result;
	}

	public void useCoupon(String id, String coupon) {
		MongoCollection<Document> issueCollection = mongoService.getIssueCollection();

		Document elemFilter = new Document(CouponField.COUPON.getField(), coupon).append(IssueField.EXPIRE_YN.getField(), false);

		Bson filter = Filters.and(Filters.eq(CommonField._ID.getField(), id),
				Filters.elemMatch(IssueField.COUPONS.getField(), elemFilter));

		Issue issue = issueCollection.find(filter, Issue.class).limit(1)
				.projection(Projections.elemMatch(IssueField.COUPONS.getField(), elemFilter)).first();
		IssueCoupon issueCoupon = Optional.ofNullable(issue).orElseThrow(() -> new NotFoundOrExpiredCouponException()).getCoupons()
				.get(0);

		if (issueCoupon.isUseYn()) {
			throw new UsedCouponException();
		} else {
			if (LocalDateTime.now().isAfter(issueCoupon.getExpireDate())) {
				issueCollection.updateOne(filter, new Document(CommonField.SET.getField(),
						new Document(IssueField.UPDATE_EXPIRE_YN.getField(), true)));
				throw new NotFoundOrExpiredCouponException();
			}
		}

		issueCollection.updateOne(filter,
				new Document(CommonField.SET.getField(), new Document(IssueField.UPDATE_USE_YN.getField(), true)));
	}

	public void cancelCoupon(String id, String coupon) {
		MongoCollection<Document> issueCollection = mongoService.getIssueCollection();

		Document elemFilter = new Document(CouponField.COUPON.getField(), coupon)
				.append(IssueField.EXPIRE_YN.getField(), false);

		Bson filter = Filters.and(Filters.eq(CommonField._ID.getField(), id),
				Filters.elemMatch(IssueField.COUPONS.getField(), elemFilter));

		Issue issue = issueCollection.find(filter, Issue.class).limit(1)
				.projection(Projections.elemMatch(IssueField.COUPONS.getField(), elemFilter)).first();
		IssueCoupon issueCoupon = Optional.ofNullable(issue).orElseThrow(() -> new NotFoundOrExpiredCouponException()).getCoupons()
				.get(0);

		if (issueCoupon.isUseYn()) {
			if (LocalDateTime.now().isAfter(issueCoupon.getExpireDate())) {
				issueCollection.updateOne(filter, new Document(CommonField.SET.getField(),
						new Document(IssueField.UPDATE_EXPIRE_YN.getField(), true)));
				throw new NotFoundOrExpiredCouponException();
			}
		} else {
			throw new CanceledCouponException();
		}

		issueCollection.updateOne(filter,
				new Document(CommonField.SET.getField(), new Document(IssueField.UPDATE_USE_YN.getField(), false)));
	}

	private Document createExpireDocument(IssueCoupon coupon, String userId) {
		Document doc = new Document();
		doc.append(CouponField.COUPON.getField(), coupon.getCoupon());
		doc.append(CouponField.USER_ID.getField(), userId);
		doc.append(IssueField.COLLECTION.getField(), coupon.getCollection());
		doc.append(CouponField.ISSUE_DATE.getField(), coupon.getIssueDate());
		doc.append(CouponField.EXPIRE_DATE.getField(), coupon.getExpireDate());
		return doc;
	}
	
	private Document createStateDocument(Coupon coupon, String userId, LocalDateTime issueDate) {
		Document doc = new Document();
		doc.append(CommonField._ID.getField(), coupon.getId());
		doc.append(CouponField.USER_ID.getField(), userId);
		doc.append(CouponField.CREATE_DATE.getField(), coupon.getCreateDate());
		doc.append(CouponField.ISSUE_DATE.getField(), issueDate);
		doc.append(CouponField.EXPIRE_DATE.getField(), coupon.getExpireDate());
		return doc;
	}

	private Coupon getUsableCoupon(List<String> notIn) {
		MongoCollection<Document> couponCollection = mongoService.getCouponCollection();
		Coupon coupon = Optional
				.ofNullable(couponCollection
						.find(Filters.and(Filters.eq(CouponField.ISSUE_YN.getField(), false),
								Filters.nin(CommonField._ID.getField(), notIn)), Coupon.class).limit(1)
						.projection(Projections.exclude(CouponField.ISSUE_YN.getField())).first())
				.orElseThrow(() -> new NotFoundUsableCouponException());
		if (coupon.getExpireDate() == null) {
			LocalDateTime now = LocalDateTime.now();
			coupon.setExpireDate(LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0, 0).plusDays(defaultExpireDay));
		}

		couponCollection.updateOne(Filters.eq(CommonField._ID.getField(), coupon.getId()),
				new Document(CommonField.SET.getField(), new Document(CouponField.ISSUE_YN.getField(), true)));
		return coupon;
	}
}
