package com.sg.assignment;

import java.time.LocalDate;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.sg.assignment.common.enums.CouponField;
import com.sg.assignment.common.enums.MongoCollections;
import com.sg.assignment.common.service.MongoService;

@SpringBootTest
public class DeleteIssuedCouponTaskTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MongoService mongoService;

	@Test
	public void task() {
		logger.info("=====================================================================================================");
		logger.info("Delete Issued Coupon From {} collection", MongoCollections.COUPON.getCollectionName());
		
		MongoCollection<Document> couponCollection = mongoService.getCouponCollection();

		DeleteResult result =  couponCollection.deleteMany(Filters.and(Filters.lt(CouponField.CREATE_DATE.getField(), LocalDate.now()),
				Filters.eq(CouponField.ISSUE_YN.getField(), true)));
		
		logger.info("Delete Count : {}", result.getDeletedCount());
		logger.info("=====================================================================================================");
	}
}
