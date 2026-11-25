package io.github.batchuka.hermes.credit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import io.github.batchuka.hermes.credit.model.Analysis;
import io.github.batchuka.hermes.credit.model.Decision;
import io.github.batchuka.hermes.credit.repository.AnalysisRepository;

@WebMvcTest(AnalysisController.class)
class AnalysisControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	AnalysisRepository repository;

	@Test
	void scoresAProposalFromIncomeAndAmount() throws Exception {
		given(repository.save(any(Analysis.class))).willAnswer(call -> call.getArgument(0));

		mockMvc.perform(post("/analyses").contentType(MediaType.APPLICATION_JSON)
				.content("{\"farmer\":\"Joao\",\"income\":50000,\"amount\":120000,\"termMonths\":24}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.score").value(41))
				.andExpect(jsonPath("$.creditLimit").value(360000.0));
	}

	@Test
	void recordsTheDecisionWithItsReason() throws Exception {
		given(repository.findById(1L)).willReturn(Optional.of(new Analysis()));
		given(repository.save(any(Analysis.class))).willAnswer(call -> call.getArgument(0));

		mockMvc.perform(put("/analyses/1/decision").contentType(MediaType.APPLICATION_JSON)
				.content("{\"decision\":\"REJECTED\",\"reason\":\"Income too low\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.decision").value("REJECTED"))
				.andExpect(jsonPath("$.reason").value("Income too low"));
	}

	@Test
	void anUnknownAnalysisIsNotFound() throws Exception {
		given(repository.findById(99L)).willReturn(Optional.empty());

		mockMvc.perform(put("/analyses/99/decision").contentType(MediaType.APPLICATION_JSON)
				.content("{\"decision\":\"APPROVED\",\"reason\":\"ok\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void aDecisionNobodyKnowsIsABadRequest() throws Exception {
		mockMvc.perform(put("/analyses/1/decision").contentType(MediaType.APPLICATION_JSON)
				.content("{\"decision\":\"MAYBE\",\"reason\":\"hmm\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void aProposalWithoutIncomeCannotBeScored() throws Exception {
		mockMvc.perform(post("/analyses").contentType(MediaType.APPLICATION_JSON)
				.content("{\"farmer\":\"Joao\",\"amount\":120000,\"termMonths\":24}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Income is required to score the proposal"));

		verifyNoInteractions(repository);
	}

	@Test
	void listsTheHistoryOfAnalyses() throws Exception {
		var analysis = new Analysis();
		analysis.setFarmer("Joao");
		given(repository.findAll()).willReturn(List.of(analysis));

		mockMvc.perform(get("/analyses"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].farmer").value("Joao"));
	}

	@Test
	void findsAnAnalysisById() throws Exception {
		var analysis = new Analysis();
		analysis.setFarmer("Joao");
		given(repository.findById(1L)).willReturn(Optional.of(analysis));

		mockMvc.perform(get("/analyses/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.farmer").value("Joao"));
	}

	@Test
	void anUnknownAnalysisIsNotFoundWhenRead() throws Exception {
		given(repository.findById(99L)).willReturn(Optional.empty());

		mockMvc.perform(get("/analyses/99")).andExpect(status().isNotFound());
	}

	@Test
	void aSecondDecisionIsRefusedWithTheExistingOne() throws Exception {
		var analysis = new Analysis();
		analysis.setDecision(Decision.APPROVED);
		analysis.setReason("ok");
		given(repository.findById(1L)).willReturn(Optional.of(analysis));

		mockMvc.perform(put("/analyses/1/decision").contentType(MediaType.APPLICATION_JSON)
				.content("{\"decision\":\"REJECTED\",\"reason\":\"changed my mind\"}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.decision").value("APPROVED"))
				.andExpect(jsonPath("$.reason").value("ok"));

		verify(repository, never()).save(any(Analysis.class));
	}

}