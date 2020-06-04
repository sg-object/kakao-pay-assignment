package com.sg.assignment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.assignment.common.jwt.JwtInfo;
import com.sg.assignment.user.model.User;

@AutoConfigureMockMvc
@SpringBootTest
public class UserRestApiTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@SuppressWarnings("deprecation")
	private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;

	@Test
	public void join() throws Exception {
		logger.info("====================================================================================================================");
		logger.info("Join Test Start!!!!!!!");
		User user = new User();
		user.setId("test");
		user.setPassword("test");
		mvc.perform(
				post("/join").content(objectMapper.writeValueAsString(user)).contentType(mediaType).accept(mediaType))
				.andDo(print());
		logger.info("Join Test End!!!!!!!");
		logger.info("====================================================================================================================");
	}

	@Test
	public void login() throws Exception {
		logger.info("====================================================================================================================");
		logger.info("Login Test Start!!!!!!!");
		User user = new User();
		user.setId("string");
		user.setPassword("string");
		mvc.perform(
				post("/login").content(objectMapper.writeValueAsString(user)).contentType(mediaType).accept(mediaType))
				.andDo(print());
		logger.info("Login Test End!!!!!!!");
		logger.info("====================================================================================================================");
	}

	@Test
	public void userList() throws Exception {
		logger.info("====================================================================================================================");
		logger.info("User List Test Start!!!!!!!");
		String jwt = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZy5vYmplY3QiLCJpYXQiOjE1OTEyNjczNjksImlkIjoic3RyaW5nIn0.oaY4H_9gKU9M_ZqwYkEmt6JKL9rCz_MCkoko0GSY63MjyfTPoLJdoduFMUHblNCiVrh4gcxrwSdseVuP2PGXjg";
		mvc.perform(get("/users").header(JwtInfo.TOKEN_NAME, jwt).contentType(mediaType).accept(mediaType))
				.andDo(print());
		logger.info("User List Test End!!!!!!!");
		logger.info("====================================================================================================================");
	}
}
