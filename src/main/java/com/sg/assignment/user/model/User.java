package com.sg.assignment.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "사용자 정보")
@Document("users")
@Data
public class User {

	@ApiModelProperty(value = "로그인 아이디")
	@Id
	private String id;

	@ApiModelProperty(value = "로그인 비밀번호")
	private String password;
}
