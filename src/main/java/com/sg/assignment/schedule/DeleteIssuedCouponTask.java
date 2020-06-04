package com.sg.assignment.schedule;

import java.time.LocalDate;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.sg.assignment.common.enums.CouponField;
import com.sg.assignment.common.enums.MongoCollections;
import com.sg.assignment.common.service.MongoService;

@Component
public class DeleteIssuedCouponTask {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MongoService mongoService;
	
	@Scheduled(cron = "0 0 1 * * *")
	private void task() {
		logger.info("=====================================================================================================");
		logger.info("Delete Issued Coupon From {} collection", MongoCollections.COUPON.getCollectionName());
		
		MongoCollection<Document> couponCollection = mongoService.getCouponCollection();

		DeleteResult result =  couponCollection.deleteMany(Filters.and(Filters.lt(CouponField.CREATE_DATE.getField(), LocalDate.now()),
				Filters.eq(CouponField.ISSUE_YN.getField(), true)));
		
		logger.info("Delete Count : {}", result.getDeletedCount());
		logger.info("=====================================================================================================");
	}
}
