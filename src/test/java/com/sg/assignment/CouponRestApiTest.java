package com.sg.assignment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.sg.data.common.jwt.JwtInfo;
import com.sg.data.coupon.model.IssueCoupon;

@AutoConfigureMockMvc
@SpringBootTest
public class CouponRestApiTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final String rootPath = "/coupons";
	
	private final String JWT = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZy5vYmplY3QiLCJpYXQiOjE1OTEyNjczNjksImlkIjoic3RyaW5nIn0.oaY4H_9gKU9M_ZqwYkEmt6JKL9rCz_MCkoko0GSY63MjyfTPoLJdoduFMUHblNCiVrh4gcxrwSdseVuP2PGXjg"; 
	
	@SuppressWarnings("deprecation")
	private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
	
	//@Test
	public void issueCoupon() {
		logger.info("====================================================================================================================");
		logger.info("Issue Coupon Test Start!!!!!!!");
		try {
			mvc.perform(
					post(rootPath + "/issue").header(JwtInfo.TOKEN_NAME, JWT).accept(mediaType))
					.andDo(print());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Issue Coupon Test End!!!!!!!");
		logger.info("====================================================================================================================");
	}
	
	//@Test
	public void myCouponList() {
		logger.info("====================================================================================================================");
		logger.info("My Coupon List Test Start!!!!!!!");
		try {
			mvc.perform(get(rootPath).queryParam("page", "1").queryParam("size", "10").header(JwtInfo.TOKEN_NAME, JWT).accept(mediaType))
					.andDo(print());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("My Coupon List Test End!!!!!!!");
		logger.info("====================================================================================================================");
	}
	
	//@Test
	public void couponState() {
		logger.info("====================================================================================================================");
		logger.info("Coupon State Test Start!!!!!!!");
		try {
			mvc.perform(get(rootPath + "/state").queryParam("coupon", "DCBF6-42738B-D4F29B04").queryParam("issueDate", "2020-06-04").header(JwtInfo.TOKEN_NAME, JWT).accept(mediaType))
					.andDo(print());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Coupon State Test End!!!!!!!");
		logger.info("====================================================================================================================");
	}
	
	//@Test
	public void useCoupon() {
		logger.info("====================================================================================================================");
		logger.info("Use Coupon Test Start!!!!!!!");
		IssueCoupon coupon = new IssueCoupon();
		coupon.setCoupon("DCBF6-42738B-D4F29B04");
		try {
			mvc.perform(put(rootPath + "/use").content(objectMapper.writeValueAsString(coupon)).header(JwtInfo.TOKEN_NAME, JWT).contentType(mediaType).accept(mediaType))
					.andDo(print());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Use Coupon Test End!!!!!!!");
		logger.info("====================================================================================================================");
	}
	
	@Test
	public void cancelCoupon() {
		logger.info("====================================================================================================================");
		logger.info("Cancel Coupon Test Start!!!!!!!");
		IssueCoupon coupon = new IssueCoupon();
		coupon.setCoupon("DCBF6-42738B-D4F29B04");
		try {
			mvc.perform(put(rootPath + "/cancellation").content(objectMapper.writeValueAsString(coupon)).header(JwtInfo.TOKEN_NAME, JWT).contentType(mediaType).accept(MediaType.APPLICATION_JSON))
					.andDo(print());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Cancel Coupon Test End!!!!!!!");
		logger.info("====================================================================================================================");
	}
}
