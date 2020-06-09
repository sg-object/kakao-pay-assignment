package com.sg.assignment.coupon.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.conversions.Bson;
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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
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
		
		// Coupon Collection 쿠폰 발급
		Query couponQuery = new Query(Criteria.where(CommonField._ID.getField()).is(issueCoupon.getCoupon()));
		Update couponUpdate = new Update();
		couponUpdate.set(CouponField.ISSUE_YN.getField(), true);
		mongoTemplate.updateFirst(couponQuery, couponUpdate, MongoCollections.COUPON.getCollectionName());
		
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
	
	public void useCoupon(String id, String coupon) {
		Query query = new Query();
		Criteria elemMatch = Criteria.where(CouponField.COUPON.getField()).is(coupon).and(IssueField.EXPIRE_YN.getField()).is(false);
		query.addCriteria(Criteria.where(CommonField._ID.getField()).is(id));
		query.addCriteria(Criteria.where(IssueField.COUPONS.getField()).elemMatch(elemMatch));
		query.fields().elemMatch(IssueField.COUPONS.getField(), elemMatch);
		Issue issue = Optional.ofNullable(mongoTemplate.findOne(query, Issue.class, MongoCollections.ISSUE.getCollectionName())).orElseThrow(() -> new NotFoundOrExpiredCouponException());
		
		IssueCoupon issueCoupon = issue.getCoupons().get(0);
		
		if (issueCoupon.isUseYn()) {
			throw new UsedCouponException();
		} else {
			if (LocalDateTime.now().isAfter(issueCoupon.getExpireDate())) {
				Update expireUpdate = new Update();
				expireUpdate.set(IssueField.UPDATE_EXPIRE_YN.getField(), true);
				mongoTemplate.updateFirst(query, expireUpdate, MongoCollections.ISSUE.getCollectionName());
				throw new NotFoundOrExpiredCouponException();
			} else {
				Update expireUpdate = new Update();
				expireUpdate.set(IssueField.UPDATE_USE_YN.getField(), true);
				mongoTemplate.updateFirst(query, expireUpdate, MongoCollections.ISSUE.getCollectionName());
			}
		}
	}
	
	/*public String issueCoupon2(String id) {
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
	}*/

	/*public List<IssueCoupon> getMyCouponList(String id, int page, int size) {
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
	}*/

	/*public void useCoupon(String id, String coupon) {
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
	}*/

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
		
		Query query = new Query(Criteria.where(CouponField.ISSUE_YN.getField()).is(false)).addCriteria(Criteria.where(CommonField._ID.getField()).nin(notIn));
		Coupon coupon = Optional.ofNullable(mongoTemplate.findOne(query, Coupon.class, MongoCollections.COUPON.getCollectionName())).orElseThrow(() -> new NotFoundUsableCouponException());
		if (coupon.getExpireDate() == null) {
			LocalDateTime now = LocalDateTime.now();
			coupon.setExpireDate(LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0, 0).plusDays(defaultExpireDay));
		}
		
		return coupon;
	}
}
