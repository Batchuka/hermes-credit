package io.github.batchuka.hermes.credit.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ScoreCalculatorTest {

	@Test
	void theScoreIsTheIncomeOverTheAmountTimesOneHundred() {
		int score = ScoreCalculator.score(new BigDecimal("50000"), new BigDecimal("120000"));

		assertThat(score).isEqualTo(41);
	}

	@Test
	void theScoreNeverPassesOneThousand() {
		int score = ScoreCalculator.score(new BigDecimal("2000000"), new BigDecimal("100000"));

		assertThat(score).isEqualTo(1000);
	}

	@Test
	void theLimitIsThirtyPercentOfTheIncomeForEachMonthOfTheTerm() {
		BigDecimal limit = ScoreCalculator.limit(new BigDecimal("50000"), 24);

		assertThat(limit).isEqualByComparingTo("360000");
	}
}