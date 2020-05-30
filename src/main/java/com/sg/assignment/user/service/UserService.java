package com.sg.assignment.user.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sg.assignment.common.exception.DuplicateUserException;
import com.sg.assignment.common.exception.VerificationException;
import com.sg.assignment.common.util.VerificationUtils;
import com.sg.assignment.user.model.User;
import com.sg.assignment.user.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public void join(User user) {
		checkUserValue(user);
		if (!userRepository.findByLoginId(user.getLoginId()).isPresent()) {
			logger.debug("join");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.insert(user);
		} else {
			throw new DuplicateUserException();
		}
	}

	public User getUser(String loginId) {
		Optional<User> user = userRepository.findByLoginId(loginId);
		if (user.isPresent()) {
			return user.get();
		} else {
			return null;
		}
	}

	private void checkUserValue(User user) {
		if (VerificationUtils.isNullOrBlank(user.getLoginId()) || VerificationUtils.isNullOrBlank(user.getPassword())) {
			throw new VerificationException();
		}
	}
}
