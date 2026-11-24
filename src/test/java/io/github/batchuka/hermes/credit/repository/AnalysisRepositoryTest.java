package io.github.batchuka.hermes.credit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import io.github.batchuka.hermes.credit.model.Analysis;
import io.github.batchuka.hermes.credit.model.Decision;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // we're using a real instance
@Testcontainers
class AnalysisRepositoryTest {

	@Container
	@ServiceConnection
	static MySQLContainer mysql = new MySQLContainer("mysql:8.4");

	@Autowired
	AnalysisRepository repository;

	@Test
	void savesAnAnalysisWithItsDecision() {
		var analysis = new Analysis();
		analysis.setFarmer("Joao");
		analysis.setIncome(new BigDecimal("50000.00"));
		analysis.setAmount(new BigDecimal("120000.00"));
		analysis.setTermMonths(24);
		analysis.setScore(41);
		analysis.setCreditLimit(new BigDecimal("360000.00"));
		analysis.setDecision(Decision.REJECTED);
		analysis.setReason("Income too low");

		var saved = repository.save(analysis);
		var found = repository.findById(saved.getId()).orElseThrow();

		assertThat(found.getDecision()).isEqualTo(Decision.REJECTED);
		assertThat(found.getReason()).isEqualTo("Income too low");
		assertThat(found.getCreditLimit()).isEqualByComparingTo("360000.00");
	}
}