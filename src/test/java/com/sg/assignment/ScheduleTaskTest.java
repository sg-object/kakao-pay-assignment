package com.sg.assignment;

import java.time.LocalDateTime;
import java.util.Arrays;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
public class ScheduleTaskTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MongoService mongoService;
	
	@Test
	public void expireNotice() {
		LocalDateTime date = LocalDateTime.now();
		String test = mongoService.createCollectionNameByExpireDate(date);
		MongoCollection<Document> expireCollection = mongoService.getCollection(test);

		Bson match = Aggregates.match(
				Filters.and(
						Filters.gte(CouponField.ISSUE_DATE.getField(), LocalDateTime.now().minusDays(1)), 
						Filters.lte(CouponField.ISSUE_DATE.getField(), LocalDateTime.now().plusDays(1)))
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
