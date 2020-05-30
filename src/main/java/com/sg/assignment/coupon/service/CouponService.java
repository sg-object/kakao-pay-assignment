package com.sg.assignment.coupon.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.sg.assignment.coupon.model.Coupon;
import com.sg.assignment.coupon.repository.CouponRepository;

@Service
public class CouponService {

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
	private CouponRepository couponRepository;

	public void createBulkCoupon(int count) {
		Set<String> coupons = new HashSet<String>();
		while (count > coupons.size()) {
			for (int i = coupons.size(); i < count; i++) {
				coupons.add(createCoupon());
			}
		}
		coupons.forEach(c -> {
			Coupon co = new Coupon();
			co.setCoupon(c);
			couponRepository.insert(co);
		});
	}

	public void createCouponByCSV(MultipartFile csv) {
		try {
			File file = getCSVFile(csv);
			BufferedReader br = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] data = line.split(comma);
				// [0] : coupon
				System.out.println(data[0].trim());
			}
			file.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
