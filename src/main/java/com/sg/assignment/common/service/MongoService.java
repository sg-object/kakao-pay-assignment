package com.sg.assignment.common.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.sg.assignment.common.enums.MongoCollections;

@Service
public class MongoService {

	@Value("${mongodb.database.kakao}")
	private String database;

	private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd");

	private final String underBar = "_";

	@Autowired
	private MongoClient client;

	public MongoCollection<Document> getCollection(String collection) {
		return client.getDatabase(database).getCollection(collection);
	};

	public MongoCollection<Document> getUserCollection() {
		return getCollection(MongoCollections.USER.getCollectionName());
	};

	public MongoCollection<Document> getCouponCollection() {
		return getCollection(MongoCollections.COUPON.getCollectionName());
	};

	public MongoCollection<Document> getIssueCollection() {
		return getCollection(MongoCollections.ISSUE.getCollectionName());
	};

	public FindIterable<Document> find(String collection, Document filter) {
		return client.getDatabase(database).getCollection(collection).find(filter);
	};

	public String createCollectionNameByIssueDate(LocalDateTime issueDate) {
		StringBuilder collectionName = new StringBuilder();
		collectionName.append(MongoCollections.COUPON.getCollectionName());
		collectionName.append(underBar);
		collectionName.append(issueDate.format(dateFormat));
		return collectionName.toString();
	}

	public String createCollectionNameByExpireDate(LocalDateTime expireDate) {
		StringBuilder collectionName = new StringBuilder();
		collectionName.append(MongoCollections.EXPIRE_COUPON.getCollectionName());
		collectionName.append(underBar);
		collectionName.append(expireDate.format(dateFormat));
		return collectionName.toString();
	}
}
