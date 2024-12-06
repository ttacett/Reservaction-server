package com.reservaction.test_sec;

import com.reservaction.test_sec.security.RsaKeysConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeysConfig.class)
public class TestSecApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestSecApplication.class, args);
	}

}
