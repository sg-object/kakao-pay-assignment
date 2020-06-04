package com.sg.assignment.schedule;

import java.time.LocalDateTime;
import java.util.Arrays;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Variable;
import com.sg.assignment.common.enums.CommonField;
import com.sg.assignment.common.enums.CouponField;
import com.sg.assignment.common.enums.IssueField;
import com.sg.assignment.common.enums.MongoCollections;
import com.sg.assignment.common.service.MongoService;

@Component
public class ExpireNoticeTask {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MongoService mongoService;
	
	@SuppressWarnings("static-access")
	@Scheduled(cron = "0 50 */1 * * *")
	private void task() {
		logger.info("Start Expire Notice Task !!!!!!!");
		LocalDateTime startDate = LocalDateTime.now().minusDays(-2);
		startDate = startDate.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), startDate.getHour(), 0, 0, 0);
		MongoCollection<Document> expireCollection = mongoService.getCollection(mongoService.createCollectionNameByExpireDate(startDate));

		Bson match = Aggregates.match(
				Filters.and(
						Filters.gte(CouponField.ISSUE_DATE.getField(), startDate), 
						Filters.lte(CouponField.ISSUE_DATE.getField(), startDate.plusHours(1)))
				);

		String id = CommonField._ID.getField();
		String userId = CouponField.USER_ID.getField();
		String collection = IssueField.COLLECTION.getField();
		String coupon = CouponField.COUPON.getField();
		String coupons = IssueField.COUPONS.getField();
		
		Bson lookup = Aggregates.lookup(
				MongoCollections.ISSUE.getCollectionName(),
				Arrays.asList(
						new Variable<>(userId, "$" + userId),
						new Variable<>(coupon, "$" + coupon),
						new Variable<>(collection, "$" + collection)),
				Arrays.asList(
						new Document("$match", new Document("$expr", new Document("$eq", Arrays.asList("$" + id, "$$" + userId)))),
						Aggregates.unwind("$" + coupons),
						new Document("$match",
								new Document("$expr",
										new Document("$and",
												Arrays.asList(
														new Document("$eq", Arrays.asList("$" + coupons + "." + coupon, "$$" + coupon)),
														new Document("$eq", Arrays.asList("$" + coupons + "." + collection, "$$" + collection)),
														new Document("$eq", Arrays.asList("$" + coupons + "." + IssueField.USE_YN.getField(), false))
														)
												)
										)
								),
						Aggregates.project(new Document(id, 1).append(coupon, "$$" + coupon))
						),
				coupon);
		
		AggregateIterable<Document> data = expireCollection
				.aggregate(Arrays.asList(match, lookup, Aggregates.unwind("$" + coupon), Aggregates.replaceRoot("$" + coupon)));
		data.forEach(c -> {
			logger.info("[{}] 님이 보유하신 쿠폰 [{}] 이(가) 3일 후에 만료 됩니다.", c.get(id), c.get(coupon));
		});
	}
}
