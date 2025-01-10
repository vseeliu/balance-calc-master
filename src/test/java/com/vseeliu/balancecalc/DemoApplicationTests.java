package com.vseeliu.balancecalc;

import com.vseeliu.balancecalc.util.AwsSqsUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.sqs.SqsClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DemoApplicationTests {
	@Autowired
	private ConfigurableApplicationContext context;

	@Test
	void contextLoads() {
		assertNotNull(context, "context should be loaded");
	}

	@Test
	void testBeansPresence() {
		assertNotNull(context.getBean(AwsSqsUtil.class), "AWSConfig bean should be loaded");
		assertNotNull(context.getBean(SqsClient.class), "SqsClient bean should be loaded");
	}
}
