package io.aftersound.weave.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;

@SpringBootApplication(
		exclude={
			CassandraDataAutoConfiguration.class
		}
)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}