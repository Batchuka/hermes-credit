package io.github.batchuka.hermes.credit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import io.github.batchuka.hermes.credit.model.Analysis;
import io.github.batchuka.hermes.credit.model.Decision;
import io.github.batchuka.hermes.credit.repository.AnalysisRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AnalysisConcurrencyTest {

	@Container
	@ServiceConnection
	static MySQLContainer mysql = new MySQLContainer("mysql:8.4");

	@Autowired
	MockMvc mockMvc;

	@Autowired
	AnalysisRepository repository;

	@Test
	void onlyOneOfTwoSimultaneousDecisionsIsStored() throws Exception {
		var analysis = new Analysis();
		analysis.setFarmer("Joao");
		Long id = repository.save(analysis).getId();

		var start = new CountDownLatch(1);
		var pool = Executors.newFixedThreadPool(2);
		var approving = pool.submit(decide(id, "APPROVED", start));
		var rejecting = pool.submit(decide(id, "REJECTED", start));

		start.countDown();
		int approvedStatus = approving.get();
		int rejectedStatus = rejecting.get();
		pool.shutdown();

		assertThat(List.of(approvedStatus, rejectedStatus)).containsExactlyInAnyOrder(200, 409);

		var stored = repository.findById(id).orElseThrow();
		var winner = approvedStatus == 200 ? Decision.APPROVED : Decision.REJECTED;
		assertThat(stored.getDecision()).isEqualTo(winner);
	}

	private Callable<Integer> decide(Long id, String decision, CountDownLatch start) {
		return () -> {
			start.await();
			return mockMvc.perform(put("/analyses/" + id + "/decision")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"decision\":\"" + decision + "\",\"reason\":\"by " + decision + "\"}"))
					.andReturn().getResponse().getStatus();
		};
	}
}