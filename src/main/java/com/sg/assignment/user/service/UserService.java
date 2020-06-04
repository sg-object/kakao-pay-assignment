package com.sg.assignment.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mongodb.client.MongoCollection;
import com.sg.assignment.common.enums.CommonField;
import com.sg.assignment.common.enums.UserField;
import com.sg.assignment.common.exception.DuplicateUserException;
import com.sg.assignment.common.exception.VerificationException;
import com.sg.assignment.common.jwt.JwtInfo;
import com.sg.assignment.common.model.Claim;
import com.sg.assignment.common.service.MongoService;
import com.sg.assignment.common.util.VerificationUtils;
import com.sg.assignment.common.web.service.TokenService;
import com.sg.assignment.user.model.User;

@Service
public class UserService {

	@Autowired
	private MongoService mongoService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private TokenService tokenService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public Map<String, String> join(User user) {
		checkUserValue(user);
		MongoCollection<Document> userCollection = mongoService.getUserCollection();
		if (userCollection.find(new Document(CommonField._ID.getField(), user.getId())).limit(1).first() == null) {
			logger.debug("join");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userCollection.insertOne(createUserDocument(user));
		} else {
			throw new DuplicateUserException();
		}
		Claim claim = new Claim();
		claim.setId(user.getId());
		Map<String, String> result = new HashMap<String, String>();
		result.put(JwtInfo.TOKEN_NAME, tokenService.createJWT(claim));
		return result;
	}

	public List<String> getUserList() {
		List<String> result = new ArrayList<String>();
		mongoService.getUserCollection().find(User.class).forEach(user -> result.add(user.getId()));
		return result;
	}

	public User getUser(String id) {
		return mongoService.getUserCollection().find(new Document(CommonField._ID.getField(), id), User.class).limit(1)
				.first();
	}

	private void checkUserValue(User user) {
		if (VerificationUtils.isNullOrBlank(user.getId()) || VerificationUtils.isNullOrBlank(user.getPassword())) {
			throw new VerificationException();
		}
	}

	private Document createUserDocument(User user) {
		Document doc = new Document();
		doc.append(CommonField._ID.getField(), user.getId());
		doc.append(UserField.PASSWORD.getField(), user.getPassword());
		return doc;
	}
}
