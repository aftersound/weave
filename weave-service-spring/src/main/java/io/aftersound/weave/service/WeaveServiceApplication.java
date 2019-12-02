package io.aftersound.weave.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;

@SpringBootApplication(
		exclude={
			CassandraDataAutoConfiguration.class
		}
)
public class WeaveServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeaveServiceApplication.class, args);
	}

}
