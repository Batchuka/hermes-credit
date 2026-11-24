package io.github.batchuka.hermes.credit.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ScoreCalculator {

	public static int score(BigDecimal income, BigDecimal amount) {
		BigDecimal ratio = income.divide(amount, 4, RoundingMode.HALF_UP);
		return Math.min(1000, ratio.multiply(BigDecimal.valueOf(100)).intValue());
	}

	public static BigDecimal limit(BigDecimal income, int termMonths) {
		return income.multiply(BigDecimal.valueOf(0.3))
				.multiply(BigDecimal.valueOf(termMonths));
	}
}