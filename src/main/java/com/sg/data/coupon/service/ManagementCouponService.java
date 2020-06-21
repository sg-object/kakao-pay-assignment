package com.sg.data.coupon.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.bulk.BulkWriteResult;
import com.sg.data.common.enums.CommonField;
import com.sg.data.common.enums.CouponField;
import com.sg.data.common.enums.MongoCollections;
import com.sg.data.common.exception.VerificationException;
import com.sg.data.common.service.MongoService;
import com.sg.data.common.util.LocalDateTimeUtils;
import com.sg.data.common.util.VerificationUtils;
import com.sg.data.coupon.model.BulkResult;
import com.sg.data.coupon.model.ExpiredCoupon;

@Service
public class ManagementCouponService {

	@Value("${coupon.section-size.first}")
	private byte firstSize;

	@Value("${coupon.section-size.second}")
	private byte secondSize;

	@Value("${coupon.section-size.third}")
	private byte thirdSize;

	@Value("${coupon.delimiter}")
	private String delimiter;

	@Value("${coupon.csv-path}")
	private String csvPath;

	private final String comma = ",";

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private MongoService mongoService;
	
	public BulkResult createBulkCoupon(int count, Date expireDate) {
		LocalDateTime createDate = LocalDateTimeUtils.plusNineHours();
		LocalDateTime initDate = null;
		if (expireDate != null) {
			initDate = LocalDateTimeUtils.ofInstant(expireDate.toInstant());
			if (createDate.isAfter(initDate)) {
				throw new VerificationException();
			}
		}
		
		BulkOperations bulk = mongoTemplate.bulkOps(BulkMode.UNORDERED, MongoCollections.COUPON.getCollectionName());

		for (int i = 0; i < count; i++) {
			String coupon = createCoupon();
			Query query = new Query(Criteria.where(CommonField._ID.getField()).is(coupon));
			Update update = upsertCoupon(createDate);
			if (initDate != null) {
				update.setOnInsert(CouponField.EXPIRE_DATE.getField(), initDate);
			}
			bulk.upsert(query, update);
		}
		
		return getBulkResult(bulk.execute(), count);
	}

	public BulkResult createBulkCouponByCSV(MultipartFile csv) {
		LocalDateTime createDate = LocalDateTimeUtils.plusNineHours();
		BulkOperations bulk = mongoTemplate.bulkOps(BulkMode.UNORDERED, MongoCollections.COUPON.getCollectionName());
		
		int count = 0;
		try {
			File file = getCSVFile(csv);
			BufferedReader br = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] data = line.split(comma);
				String coupon = data[0];
				if (VerificationUtils.isValidCoupon(coupon)) {
					Query query = new Query(Criteria.where(CommonField._ID.getField()).is(coupon));
					Update update = upsertCoupon(createDate);
					bulk.upsert(query, update);
					++count;
				}
			}
			file.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return getBulkResult(bulk.execute(), count);
	}

	public List<ExpiredCoupon> getExpiredCouponAtDate(int page, int size, LocalDateTime date) {
		if (page < 1 || size < 1) {
			throw new VerificationException();
		}
		Query query = new Query(Criteria.where(CouponField.EXPIRE_DATE.getField()).lte(date));
		query.with(PageRequest.of(page - 1, size, Sort.by(Direction.DESC, CouponField.EXPIRE_DATE.getField())));
		return mongoTemplate.find(query, ExpiredCoupon.class, mongoService.createCollectionNameByExpireDate(date));
	}

	private BulkResult getBulkResult(BulkWriteResult bulkWriteResult, int totalCount) {
		BulkResult bulkResult = new BulkResult();
		bulkResult.setTotalCount(totalCount);
		bulkResult.setCreateCount(totalCount - bulkWriteResult.getMatchedCount());
		bulkResult.setDuplicateCount(bulkWriteResult.getMatchedCount());
		return bulkResult;
	}
	
	private File getCSVFile(MultipartFile csv) throws IOException {
		File dir = new File(csvPath);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}
		File file = new File(dir, csv.getOriginalFilename());
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(csv.getBytes());
		fos.close();
		return file;
	}

	private Update upsertCoupon(LocalDateTime createDate) {
		Update update = new Update();
		update.setOnInsert(CouponField.CREATE_DATE.getField(), createDate);
		update.setOnInsert(CouponField.ISSUE_YN.getField(), false);
		return update;
	}

	private String createCoupon() {
		String uuid = UUID.randomUUID().toString().toUpperCase().replaceAll(delimiter, "");
		StringBuilder coupon = new StringBuilder();
		coupon.append(uuid.substring(0, firstSize));
		coupon.append(delimiter);
		coupon.append(uuid.substring(firstSize, firstSize + secondSize));
		coupon.append(delimiter);
		coupon.append(uuid.substring(firstSize + secondSize, firstSize + secondSize + thirdSize));
		return coupon.toString();
	}
}
