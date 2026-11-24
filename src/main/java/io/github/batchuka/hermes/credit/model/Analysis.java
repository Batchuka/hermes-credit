package io.github.batchuka.hermes.credit.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "analyses")
public class Analysis {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String farmer;
	private BigDecimal income;
	private BigDecimal amount;
	private Integer termMonths;
	private Integer score;
	private BigDecimal creditLimit;

	public Long getId() { return id; }

	public String getFarmer() { return farmer; }
	public void setFarmer(String farmer) { this.farmer = farmer; }

	public BigDecimal getIncome() { return income; }
	public void setIncome(BigDecimal income) { this.income = income; }

	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }

	public Integer getTermMonths() { return termMonths; }
	public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }

	public Integer getScore() { return score; }
	public void setScore(Integer score) { this.score = score; }

	public BigDecimal getCreditLimit() { return creditLimit; }
	public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }
}