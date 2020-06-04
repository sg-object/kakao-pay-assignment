package com.sg.assignment.user.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.sg.assignment.user.model.User;
import com.sg.assignment.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(description = "사용자(가입, 로그인, 전체 조회(테스트용))")
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@ApiOperation(value = "회원 가입", notes = "회원 가입 완료 시 JWT 바로 발급")
	@PostMapping("/join")
	public Map<String, String> join(@RequestBody User user) {
		return userService.join(user);
	}

	@ApiOperation(value = "로그인", notes = "로그인 성공 시 JWT 발급")
	@PostMapping("/login")
	public void login(@RequestBody User user) {
		// swagger test를 위한 source 실제 로직은 spring security에서 실행
	}

	@ApiOperation(value = "전체 사용자 목록 조회(테스트용)", notes = "아이디만 출력")
	@GetMapping("/users")
	public List<String> getUserList() {
		return userService.getUserList();
	}
}
