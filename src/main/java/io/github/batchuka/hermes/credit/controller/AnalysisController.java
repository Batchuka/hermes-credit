package io.github.batchuka.hermes.credit.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.batchuka.hermes.credit.model.Analysis;
import io.github.batchuka.hermes.credit.model.Decision;
import io.github.batchuka.hermes.credit.repository.AnalysisRepository;

@RestController
@RequestMapping("/analyses")
public class AnalysisController {

	private final AnalysisRepository repository;

	public AnalysisController(AnalysisRepository repository) {
		this.repository = repository;
	}

	record DecisionRequest(Decision decision, String reason) {
	}

	@PostMapping
	public Analysis create(@RequestBody Analysis analysis) {
		BigDecimal ratio = analysis.getIncome()
				.divide(analysis.getAmount(), 4, RoundingMode.HALF_UP);
		int score = Math.min(1000, ratio.multiply(BigDecimal.valueOf(100)).intValue());
		BigDecimal limit = analysis.getIncome()
				.multiply(BigDecimal.valueOf(0.3))
				.multiply(BigDecimal.valueOf(analysis.getTermMonths()));

		analysis.setScore(score);
		analysis.setCreditLimit(limit);
		return repository.save(analysis);
	}

	@PutMapping("/{id}/decision")
	public ResponseEntity<Analysis> decide(@PathVariable Long id,@RequestBody DecisionRequest request) {
		return repository.findById(id)
				.map(analysis -> {
					analysis.setDecision(request.decision());
					analysis.setReason(request.reason());
					return ResponseEntity.ok(repository.save(analysis));
				})
				.orElse(ResponseEntity.notFound().build());
	}

}