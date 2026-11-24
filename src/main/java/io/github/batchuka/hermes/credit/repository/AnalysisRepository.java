package io.github.batchuka.hermes.credit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.batchuka.hermes.credit.model.Analysis;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
}