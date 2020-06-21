package com.sg.assignment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.sg.data.common.jwt.JwtInfo;

@AutoConfigureMockMvc
@SpringBootTest
public class ManagementRestApiTest {

	@Autowired
	private MockMvc mvc;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final String rootPath = "/management/coupons";
	
	private final String JWT = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzZy5vYmplY3QiLCJpYXQiOjE1OTEyNjczNjksImlkIjoic3RyaW5nIn0.oaY4H_9gKU9M_ZqwYkEmt6JKL9rCz_MCkoko0GSY63MjyfTPoLJdoduFMUHblNCiVrh4gcxrwSdseVuP2PGXjg"; 
	
	@SuppressWarnings("deprecation")
	private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
	
	//@Test
	public void bulkByApi() throws Exception {
		logger.info("====================================================================================================================");
		logger.info("Bulk Test Start!!!!!!!");
		mvc.perform(
				post(rootPath + "/bulk").queryParam("count", "100").queryParam("expireDate", "2020060423").header(JwtInfo.TOKEN_NAME, JWT).accept(mediaType))
				.andDo(print());
		logger.info("Bulk Test End!!!!!!!");
		logger.info("====================================================================================================================");
	}

	@Test
	public void bulkByCSV() {
		logger.info("====================================================================================================================");
		logger.info("Bulk CSV Test Start!!!!!!!");
		try {
			File file = new File("src/test/resources/coupon.csv");
			FileInputStream fi = new FileInputStream(file);
			MockMultipartFile mock = new MockMultipartFile("csv", file.getName(), "multipart/form-data",fi);
			mvc.perform(fileUpload(rootPath + "/bulk/csv").file(mock).header(JwtInfo.TOKEN_NAME, JWT).accept(mediaType)).andExpect(status().isOk()).andDo(print());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Bulk CSV Test End!!!!!!!");
		logger.info("====================================================================================================================");
	}

	//@Test
	public void expiredCouponAtToday() throws Exception {
		logger.info("====================================================================================================================");
		logger.info("Expired At Today Test Start!!!!!!!");
		mvc.perform(get(rootPath + "/expiration").header(JwtInfo.TOKEN_NAME, JWT).queryParam("page", "1").queryParam("size", "100").accept(mediaType))
				.andDo(print());
		logger.info("Expired At Today Test End!!!!!!!");
		logger.info("====================================================================================================================");
	}
}
