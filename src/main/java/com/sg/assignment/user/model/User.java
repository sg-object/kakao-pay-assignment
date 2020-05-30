package com.sg.assignment.user.model;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document("users")
@Data
public class User {

	private String loginId;

	private String password;
}
