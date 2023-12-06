package com.example.rewards;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.rewards.model.Transaction;

class CalculatorTests {
	
	static CalculatorApiDelegateImpl calc;
	
	@BeforeAll
	static void init() {
		calc  = new CalculatorApiDelegateImpl();
		calc.bracketOneMultiplier = 1;
		calc.bracketOneStart = 50d;
		calc.bracketTwoMultiplier = 2;
		calc.bracketTwoStart = 100d;
	}

	@Test
	@DisplayName("Given a transaction less than 0 award 0 points")
	void transactionValueLessThanZero() {
		Transaction transaction = new Transaction();
		transaction.setAmount(-150d);
		assertThat(calc.calculatePoints(transaction)).isZero();
	}

	@Test
	@DisplayName("Given a transaction of $50 or less award 0 points")
	void transactionValue50OrLess() {
		Transaction transaction = new Transaction();
		transaction.setAmount(50d);
		assertThat(calc.calculatePoints(transaction)).isZero();
	}

	@Test
	@DisplayName("Given a transaction of $51 award 1 points")
	void transactionValue51() {
		Transaction transaction = new Transaction();
		transaction.setAmount(51d);
		assertThat(calc.calculatePoints(transaction)).isEqualTo(1);
	}

	@Test
	@DisplayName("Given a transaction of $100 award 50 points")
	void transactionValue100() {
		Transaction transaction = new Transaction();
		transaction.setAmount(100d);
		assertThat(calc.calculatePoints(transaction)).isEqualTo(50);
	}

	@Test
	@DisplayName("Given a transaction of $60 award 10 points")
	void transactionValue60() {
		Transaction transaction = new Transaction();
		transaction.setAmount(60d);
		assertThat(calc.calculatePoints(transaction)).isEqualTo(10);
	}

	@Test
	@DisplayName("Given a transaction of $101 award 52 points.")
	void transactionValue101() {
		Transaction transaction = new Transaction();
		transaction.setAmount(101d);
		assertThat(calc.calculatePoints(transaction)).isEqualTo(52);
	}

	@Test
	@DisplayName("Given a transaction of $120 award 90 points.")
	void transactionValue120() {
		Transaction transaction = new Transaction();
		transaction.setAmount(120d);
		assertThat(calc.calculatePoints(transaction)).isEqualTo(90);
	}
}
