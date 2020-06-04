package com.sg.assignment.config;

import java.util.Arrays;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoDBConfig {

	@Value("${mongodb.host}")
	private String host;

	@Value("${mongodb.port}")
	private int port;

	@Value("${mongodb.userName}")
	private String userName;

	@Value("${mongodb.database.auth}")
	private String authdb;

	@Value("${mongodb.password}")
	private String password;

	@Bean
	public MongoClient mongoClient() {
		MongoCredential credential = MongoCredential.createCredential(userName, authdb, password.toCharArray());
		CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		MongoClientSettings settings = MongoClientSettings.builder().credential(credential)
				.applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress(host, port))))
				.codecRegistry(pojoCodecRegistry).build();
		return MongoClients.create(settings);
	}
}
