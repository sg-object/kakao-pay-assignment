package com.sg.assignment.coupon.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.sg.assignment.common.enums.CommonField;
import com.sg.assignment.common.enums.CouponField;
import com.sg.assignment.common.enums.MongoCollections;
import com.sg.assignment.common.exception.VerificationException;
import com.sg.assignment.common.service.MongoService;
import com.sg.assignment.common.util.VerificationUtils;
import com.sg.assignment.coupon.model.BulkResult;
import com.sg.assignment.coupon.model.ExpiredCoupon;

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
	private MongoService mongoService;

	public BulkResult createBulkCoupon(int count, Date expireDate) {
		LocalDateTime createDate = LocalDateTime.now();
		LocalDateTime initDate = null;
		if (expireDate != null) {
			initDate = LocalDateTime.ofInstant(expireDate.toInstant(), ZoneId.systemDefault());
			if (createDate.isAfter(initDate)) {
				throw new VerificationException();
			}
		}

		List<UpdateOneModel<Document>> docs = new ArrayList<UpdateOneModel<Document>>();
		UpdateOptions updateOptions = new UpdateOptions().upsert(true);
		for (int i = 0; i < count; i++) {
			Document document = createCouponDocument(createDate, true);
			if (initDate != null) {
				document.append(CouponField.EXPIRE_DATE.getField(), initDate);
			}
			docs.add(new UpdateOneModel<Document>(
					Filters.eq(CommonField._ID.getField(), document.get(CommonField._ID.getField())),
					new Document(CommonField.SET_ON_INSERT.getField(), document), updateOptions));
		}
		BulkWriteResult bulkWriteResult = mongoService.getCollection(MongoCollections.COUPON.getCollectionName())
				.bulkWrite(docs, new BulkWriteOptions().ordered(false));
		BulkResult bulkResult = new BulkResult();
		bulkResult.setTotalCount(count);
		bulkResult.setCreateCount(count - bulkWriteResult.getMatchedCount());
		bulkResult.setDuplicateCount(bulkWriteResult.getMatchedCount());
		return bulkResult;
	}

	public BulkResult createBulkCouponByCSV(MultipartFile csv) {
		LocalDateTime createDate = LocalDateTime.now();
		List<UpdateOneModel<Document>> docs = new ArrayList<UpdateOneModel<Document>>();
		UpdateOptions updateOptions = new UpdateOptions().upsert(true);
		try {
			File file = getCSVFile(csv);
			BufferedReader br = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] data = line.split(comma);
				String coupon = data[0];
				if (VerificationUtils.isValidCoupon(coupon)) {
					Document document = createCouponDocument(createDate, false);
					document.append(CommonField._ID.getField(), coupon);
					docs.add(new UpdateOneModel<Document>(Filters.eq(CommonField._ID.getField(), coupon),
							new Document(CommonField.SET_ON_INSERT.getField(), document), updateOptions));
				}
			}
			file.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BulkWriteResult bulkWriteResult = mongoService.getCollection(MongoCollections.COUPON.getCollectionName())
				.bulkWrite(docs, new BulkWriteOptions().ordered(false));
		BulkResult bulkResult = new BulkResult();
		bulkResult.setTotalCount(docs.size());
		bulkResult.setCreateCount(docs.size() - bulkWriteResult.getMatchedCount());
		bulkResult.setDuplicateCount(bulkWriteResult.getMatchedCount());
		return bulkResult;
	}

	public List<ExpiredCoupon> getExpiredCouponAtToday(int page, int size) {
		if (page < 1 || size < 1) {
			throw new VerificationException();
		}
		LocalDateTime now = LocalDateTime.now();
		FindIterable<ExpiredCoupon> coupons = mongoService
				.getCollection(mongoService.createCollectionNameByExpireDate(now))
				.find(Filters.lte(CouponField.EXPIRE_DATE.getField(), now), ExpiredCoupon.class)
				.sort(new Document(CouponField.EXPIRE_DATE.getField(), -1)).skip((page - 1) * size).limit(size);
		List<ExpiredCoupon> result = new ArrayList<ExpiredCoupon>();
		if (coupons != null) {
			coupons.forEach(coupon -> {
				result.add(coupon);
			});
		}
		return result;
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

	private Document createCouponDocument(LocalDateTime createDate, boolean initCoupon) {
		Document doc = new Document();
		if (initCoupon) {
			doc.append(CommonField._ID.getField(), createCoupon());
		}
		doc.append(CouponField.CREATE_DATE.getField(), createDate);
		doc.append(CouponField.ISSUE_YN.getField(), false);
		return doc;
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
