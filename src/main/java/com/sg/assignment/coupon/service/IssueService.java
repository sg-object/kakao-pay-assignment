package com.sg.assignment.coupon.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ReplaceRootOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sg.assignment.common.enums.CommonField;
import com.sg.assignment.common.enums.CouponField;
import com.sg.assignment.common.exception.CanceledCouponException;
import com.sg.assignment.common.exception.NotFoundOrExpiredCouponException;
import com.sg.assignment.common.exception.NotFoundUsableCouponException;
import com.sg.assignment.common.exception.UsedCouponException;
import com.sg.assignment.common.enums.IssueField;
import com.sg.assignment.common.enums.MongoCollections;
import com.sg.assignment.common.service.MongoService;
import com.sg.assignment.coupon.model.Coupon;
import com.sg.assignment.coupon.model.CouponState;
import com.sg.assignment.coupon.model.Issue;
import com.sg.assignment.coupon.model.IssueCoupon;

@Service
public class IssueService {

	@Value("${coupon.default-expire-day}")
	private int defaultExpireDay;
	
	@Autowired
	private MongoService mongoService;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	public IssueCoupon issueCoupon(String id) {
		LocalDateTime issueDate = LocalDateTime.now();
		
		// 유효 쿠폰 발급 및 collection 생성
		Coupon coupon = getUsableCoupon(id);
		String stateCollectionName = mongoService.createCollectionNameByIssueDate(issueDate);
		String expireCollectionName = mongoService.createCollectionNameByExpireDate(coupon.getExpireDate());
		createCollection(stateCollectionName);
		createCollection(expireCollectionName);
				
		// 발급 쿠폰 정보 생성
		IssueCoupon issueCoupon = new IssueCoupon(coupon);
		issueCoupon.setUseYn(false);
		issueCoupon.setExpireYn(false);
		issueCoupon.setCollection(stateCollectionName);
		issueCoupon.setExpireCollection(expireCollectionName);
		issueCoupon.setIssueDate(issueDate);
		issueCoupon.setUserId(id);

		return issueCoupon;
	}
	
	private void createCollection(String collectionName) {
		if(!mongoTemplate.collectionExists(collectionName)) {
			mongoTemplate.createCollection(collectionName);
		}
	}

	@Transactional
	public void issueCoupon(IssueCoupon issueCoupon) {
		// Issue Collection 쿠폰 발급
		Query issueQuery = new Query(Criteria.where(CommonField._ID.getField()).is(issueCoupon.getUserId()));
		Update issueUpdate = new Update();
		issueUpdate.addToSet(IssueField.COUPONS.getField(), issueCoupon);
		mongoTemplate.upsert(issueQuery, issueUpdate, MongoCollections.ISSUE.getCollectionName());
		
		// 쿠폰 관리 Collection
		mongoTemplate.insert(new CouponState(issueCoupon), issueCoupon.getCollection());
		// 쿠폰 만료 관리 Collection
		mongoTemplate.insert(new CouponState(issueCoupon), issueCoupon.getExpireCollection());
	}

	public List<IssueCoupon> getMyCouponList(String id, int page, int size) {
		MatchOperation match = Aggregation.match(Criteria.where(CommonField._ID.getField()).is(id));
		UnwindOperation unwind = Aggregation.unwind(IssueField.COUPONS.getField());
		SortOperation sort = Aggregation.sort(Sort.Direction.DESC, IssueField.COUPONS_ISSUE_DATE.getField());
		SkipOperation skip = Aggregation.skip(new Long(((page-1) * size)));
		LimitOperation limit = Aggregation.limit(size);
		ReplaceRootOperation replace = Aggregation.replaceRoot(IssueField.COUPONS.getField());

		Aggregation aggregation = Aggregation.newAggregation(match, unwind, sort, skip, limit, replace);
		AggregationResults<IssueCoupon> result = mongoTemplate.aggregate(aggregation, MongoCollections.ISSUE.getCollectionName(), IssueCoupon.class);
		return result.getMappedResults();
	}
	
	@Transactional
	public boolean useCoupon(String id, String coupon) {
		Query query = getCouponQuery(id, coupon);
		IssueCoupon issueCoupon = getIssueCoupon(query);
		if (issueCoupon.isUseYn()) {
			throw new UsedCouponException();
		} else {
			if (LocalDateTime.now().isAfter(issueCoupon.getExpireDate())) {
				updateExpireYn(query);
				return false;
			} else {
				updateUseYn(query, true);
				return true;
			}
		}
	}
	
	@Transactional
	public boolean cancelCoupon(String id, String coupon) {
		Query query = getCouponQuery(id, coupon);
		IssueCoupon issueCoupon = getIssueCoupon(query);
		if (issueCoupon.isUseYn()) {
			if (LocalDateTime.now().isAfter(issueCoupon.getExpireDate())) {
				updateExpireYn(query);
				return false;
			}else {
				updateUseYn(query, false);
				return true;
			}
		} else {
			throw new CanceledCouponException();
		}
	}
	
	private void updateUseYn(Query query, boolean useYn) {
		Update update = new Update();
		update.set(IssueField.UPDATE_USE_YN.getField(), useYn);
		mongoTemplate.updateFirst(query, update, MongoCollections.ISSUE.getCollectionName());
	}
	
	private void updateExpireYn(Query query) {
		Update update = new Update();
		update.set(IssueField.UPDATE_EXPIRE_YN.getField(), true);
		mongoTemplate.updateFirst(query, update, MongoCollections.ISSUE.getCollectionName());
	}
	
	private IssueCoupon getIssueCoupon(Query query) {
		Issue issue = Optional
				.ofNullable(mongoTemplate.findOne(query, Issue.class, MongoCollections.ISSUE.getCollectionName()))
				.orElseThrow(() -> new NotFoundOrExpiredCouponException());
		return issue.getCoupons().get(0);
	}

	private Query getCouponQuery(String id, String coupon) {
		Query query = new Query();
		Criteria elemMatch = Criteria.where(CouponField.COUPON.getField()).is(coupon)
				.and(IssueField.EXPIRE_YN.getField()).is(false);
		query.addCriteria(Criteria.where(CommonField._ID.getField()).is(id));
		query.addCriteria(Criteria.where(IssueField.COUPONS.getField()).elemMatch(elemMatch));
		query.fields().elemMatch(IssueField.COUPONS.getField(), elemMatch);
		return query;
	}
	
	private Coupon getUsableCoupon(String id) {
		MatchOperation idMatch = Aggregation.match(Criteria.where(CommonField._ID.getField()).is(id));
		UnwindOperation unwind = Aggregation.unwind(IssueField.COUPONS.getField());
		MatchOperation dateMatch = Aggregation.match(Criteria.where(IssueField.COUPONS_EXPIRE_DATE.getField()).gte(LocalDate.now()));
		GroupOperation group = Aggregation.group(IssueField.COUPONS_COUPON.getField());
		Aggregation aggregation = Aggregation.newAggregation(idMatch, unwind, dateMatch, group);
		AggregationResults<Coupon> result = mongoTemplate.aggregate(aggregation, MongoCollections.ISSUE.getCollectionName(), Coupon.class);
		
		List<String> notIn = new ArrayList<String>();
		result.getMappedResults().forEach(coupon -> {
			notIn.add(coupon.getId());
		});
		
		Query query = new Query(Criteria.where(CouponField.ISSUE_YN.getField()).is(false))
				.addCriteria(Criteria.where(CommonField._ID.getField()).nin(notIn));
		Update update = new Update();
		update.set(CouponField.ISSUE_YN.getField(), true);
		Coupon coupon = Optional
				.ofNullable(mongoTemplate.findAndModify(query, update, Coupon.class,
						MongoCollections.COUPON.getCollectionName()))
				.orElseThrow(() -> new NotFoundUsableCouponException());
		if (coupon.getExpireDate() == null) {
			LocalDateTime now = LocalDateTime.now();
			coupon.setExpireDate(
					LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0, 0)
							.plusDays(defaultExpireDay));
		}
		
		/*Query query = new Query(Criteria.where(CouponField.ISSUE_YN.getField()).is(false)).addCriteria(Criteria.where(CommonField._ID.getField()).nin(notIn));
		Coupon coupon = Optional.ofNullable(mongoTemplate.findOne(query, Coupon.class, MongoCollections.COUPON.getCollectionName())).orElseThrow(() -> new NotFoundUsableCouponException());
		if (coupon.getExpireDate() == null) {
			LocalDateTime now = LocalDateTime.now();
			coupon.setExpireDate(LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0, 0).plusDays(defaultExpireDay));
		}
		
		// Coupon Collection 쿠폰 발급
		Query couponQuery = new Query(Criteria.where(CommonField._ID.getField()).is(coupon.getId()));
		Update couponUpdate = new Update();
		couponUpdate.set(CouponField.ISSUE_YN.getField(), true);
		mongoTemplate.updateFirst(couponQuery, couponUpdate, MongoCollections.COUPON.getCollectionName());*/
		return coupon;
	}
}
