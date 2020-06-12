package com.sg.assignment.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoDBConfig extends AbstractMongoClientConfiguration {

	@Value("${spring.data.mongodb.cluster}")
	private List<String> cluster;

	@Value("${spring.data.mongodb.database}")
	private String database;

	@Bean
	public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
		return new MongoTransactionManager(dbFactory);
	}

	@Override
	protected String getDatabaseName() {
		// TODO Auto-generated method stub
		return database;
	}

	@Bean
	@Override
	public MongoClient mongoClient() {
		// TODO Auto-generated method stub
		return MongoClients.create(mongoClientSettings());
	}

	@Bean
	public MongoClientSettings mongoClientSettings() {
		List<ServerAddress> hosts = new ArrayList<>();
		cluster.forEach(url -> {
			String[] info = url.split(":");
			hosts.add(new ServerAddress(info[0], Integer.parseInt(info[1])));
		});
		return MongoClientSettings.builder().applyToClusterSettings(builder -> builder.hosts(hosts)).build();
	}
}
