package com.sg.data.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sg.data.common.exception.DuplicateUserException;
import com.sg.data.common.exception.VerificationException;
import com.sg.data.common.jwt.JwtInfo;
import com.sg.data.common.model.Claim;
import com.sg.data.common.util.VerificationUtils;
import com.sg.data.common.web.service.TokenService;
import com.sg.data.user.model.User;
import com.sg.data.user.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UserRepository userRepository;

	@Transactional
	public Map<String, String> join(User user) {
		checkUserValue(user);
		if (userRepository.findById(user.getId()).isPresent()) {
			throw new DuplicateUserException();
		} else {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.insert(user);
		}
		Claim claim = new Claim();
		claim.setId(user.getId());
		Map<String, String> result = new HashMap<String, String>();
		result.put(JwtInfo.TOKEN_NAME, tokenService.createJWT(claim));
		return result;
	}

	public User getUser(String id) {
		return userRepository.findById(id).orElseGet(null);
	}

	public List<String> getUserList() {
		List<String> result = new ArrayList<String>();
		userRepository.findAll().forEach(user -> {
			result.add(user.getId());
		});
		return result;
	}

	private void checkUserValue(User user) {
		if (VerificationUtils.isNullOrBlank(user.getId()) || VerificationUtils.isNullOrBlank(user.getPassword())) {
			throw new VerificationException();
		}
	}
}
