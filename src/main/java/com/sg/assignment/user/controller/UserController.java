package com.sg.assignment.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.sg.assignment.user.model.User;
import com.sg.assignment.user.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/join")
	public void join(@RequestBody User user) {
		userService.join(user);
	}

	@PostMapping("/login")
	public void login(@RequestBody User user) {
		// swagger test를 위한 source 실제 로직은 spring security에서 실행
	}
}
