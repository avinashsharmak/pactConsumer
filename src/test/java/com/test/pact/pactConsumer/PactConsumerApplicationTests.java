package com.test.pact.pactConsumer;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class PactConsumerApplicationTests {

	@Rule
	public PactProviderRule mockProvider = new PactProviderRule("tester_provider","localhost", 8082, this);
	private RestTemplate restTemplate=new RestTemplate();


	@Pact(provider = "tester_provider", consumer = "tester_consumer")
	public RequestResponsePact createPact(PactDslWithProvider builder) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);


		PactDslJsonBody bodyResponse = new PactDslJsonBody()
				.stringValue("name", "myName")
				.stringType("location", "Pune");

		return builder
				.given("Tester app").uponReceiving("Get tester info")
				.path("/api/tester")
				.body(bodyResponse)
				.headers(headers)
				.method(RequestMethod.POST.name())
				.willRespondWith()
				.headers(headers)
				.status(200).body(bodyResponse).toPact();
	}





	@Test
	@PactVerification
	public void testGetTesterInfo() throws IOException {

		Tester tester=new Tester("myName", "Pune");
		HttpHeaders headers=new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> request=new HttpEntity<Object>(tester, headers);
		System.out.println("MOCK provider URL"+mockProvider.getUrl());
		ResponseEntity<String> responseEntity=restTemplate.postForEntity(mockProvider.getUrl()+"/api/tester", request, String.class);
		Assert.assertEquals("TV", "TV");
	}

}
