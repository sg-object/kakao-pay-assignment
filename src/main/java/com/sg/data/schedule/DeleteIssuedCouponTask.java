package com.sg.data.schedule;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.mongodb.client.result.DeleteResult;
import com.sg.data.common.enums.CouponField;
import com.sg.data.common.enums.MongoCollections;

@Component
public class DeleteIssuedCouponTask {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Scheduled(cron = "0 0 1 * * *")
	private void task() {
		logger.info("=====================================================================================================");
		logger.info("Delete Issued Coupon From {} collection", MongoCollections.COUPON.getCollectionName());

		Query query = new Query();
		query.addCriteria(Criteria.where(CouponField.CREATE_DATE.getField()).lt(LocalDate.now()));
		query.addCriteria(Criteria.where(CouponField.ISSUE_YN.getField()).is(true));

		DeleteResult result = mongoTemplate.remove(query, MongoCollections.COUPON.getCollectionName());

		logger.info("Delete Count : {}", result.getDeletedCount());
		logger.info("=====================================================================================================");
	}
}
