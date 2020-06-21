package com.sg.data.common.jwt;

import java.io.UnsupportedEncodingException;
import io.jsonwebtoken.SignatureAlgorithm;

public abstract class JwtInfo {

	public static final String TOKEN_NAME = "jwt-token";

	public static final String REFRESH_TOKEN = "refresh-token";

	public static final String ISSUER = "sg.object";

	public static final String SECRET_KEY = "secret";

	public static final int EXPIRES_LIMIT = 3;

	public static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.HS512;

	public static byte[] getSecretKey() throws UnsupportedEncodingException {
		return SECRET_KEY.getBytes("UTF-8");
	}
}
