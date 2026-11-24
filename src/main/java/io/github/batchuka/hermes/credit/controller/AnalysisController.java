package io.github.batchuka.hermes.credit.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.batchuka.hermes.credit.model.Analysis;
import io.github.batchuka.hermes.credit.model.Decision;
import io.github.batchuka.hermes.credit.repository.AnalysisRepository;
import io.github.batchuka.hermes.credit.service.ScoreCalculator;

@RestController
@RequestMapping("/analyses")
public class AnalysisController {

	private final AnalysisRepository repository;

	public AnalysisController(AnalysisRepository repository) {
		this.repository = repository;
	}

	record DecisionRequest(Decision decision, String reason) {
	}


	@GetMapping
	public List<Analysis> list() {
		return repository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Analysis> get(@PathVariable Long id) {
		return repository.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody Analysis analysis) {

		if (analysis.getIncome() == null) {
			return ResponseEntity.badRequest().body(ProblemDetail.forStatusAndDetail(
					HttpStatus.BAD_REQUEST, "Income is required to score the proposal"));
		}

		int score = ScoreCalculator.score(analysis.getIncome(), analysis.getAmount());
		BigDecimal limit = ScoreCalculator.limit(analysis.getIncome(), analysis.getTermMonths());

		analysis.setScore(score);
		analysis.setCreditLimit(limit);
		return ResponseEntity.ok(repository.save(analysis));
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