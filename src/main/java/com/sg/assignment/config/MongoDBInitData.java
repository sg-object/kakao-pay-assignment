package com.sg.assignment.config;

import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import com.sg.assignment.common.enums.MongoCollections;

@Component
public class MongoDBInitData {

	@Autowired
	private MongoTemplate mongoTemplate;

	@PostConstruct
	private void init() {
		List<String> collections = Arrays.asList(
				MongoCollections.USER.getCollectionName(),
				MongoCollections.COUPON.getCollectionName(),
				MongoCollections.ISSUE.getCollectionName());
		collections.forEach(collection -> {
			if (!mongoTemplate.collectionExists(collection)) {
				mongoTemplate.createCollection(collection);
			}
		});
	}
}
