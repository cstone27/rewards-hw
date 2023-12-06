/**
 * 
 */
package com.example.rewards;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.rewards.model.CalculatePoints200Response;
import com.example.rewards.model.Points;
import com.example.rewards.model.Transaction;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalculatorApiDelegateImpl implements CalculatorApiDelegate {
	@Value("${calculator.bracket1.start:50}")
	Double bracketOneStart;
	@Value("${calculator.bracket2.start:100}")
	Double bracketTwoStart;
	@Value("${calculator.bracket1.multiplier:1}")
	Integer bracketOneMultiplier;
	@Value("${calculator.bracket2.multiplier:2}")
	Integer bracketTwoMultiplier;

	@Override
	public ResponseEntity<CalculatePoints200Response> calculatePoints(List<Transaction> transactions) {
		CalculatePoints200Response response = new CalculatePoints200Response();
		List<Points> monthlyPoints = summarizePointsByMonth(transactions);
		monthlyPoints.sort(new Comparator<Points>() {
			@Override
			public int compare(Points o1, Points o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});
		for (Points points : monthlyPoints) {
			response.addMonthlyItem(points);
		}
		response.setTotal(totalPoints(monthlyPoints));
		return ResponseEntity.ok(response);
	}

	protected List<Points> summarizePointsByMonth(List<Transaction> transactions) {
		List<Points> pointsByMonth = new ArrayList<>();
		Map<LocalDate, List<Transaction>> transactionMap = transactions.stream()
				.collect(Collectors.groupingBy(t -> LocalDate.of(t.getDate().getYear(), t.getDate().getMonthValue(), 1)));
		for (Map.Entry<LocalDate, List<Transaction>> monthTransactions : transactionMap.entrySet()) {
			LocalDate month = monthTransactions.getKey();
			List<Transaction> transactionsForMonth = monthTransactions.getValue();
			Integer totalForMonth = 0;
			for (Transaction transaction : transactionsForMonth) {
				totalForMonth += calculatePoints(transaction);
			}
			Points points = new Points();
			points.setAmount(totalForMonth);
			points.setDate(month);
			pointsByMonth.add(points);
		}
		return pointsByMonth;
	}

	protected Integer calculatePoints(Transaction transaction) {
		int total = calculateBracketPoints(transaction, bracketOneStart, bracketTwoStart, bracketOneMultiplier);
		total += calculateBracketPoints(transaction, bracketTwoStart, Double.MAX_VALUE, bracketTwoMultiplier);
		log.debug("{} points for transaction {}", total, transaction);
		return total;
	}

	protected Integer calculateBracketPoints(Transaction transaction, Double bracketStart, Double bracketEnd,
			Integer bracketMultiplier) {
		double bracketOneDollars = Double.min(bracketEnd - bracketStart, transaction.getAmount() - bracketStart);
		if (bracketOneDollars > 0) {
			return (int) bracketOneDollars * bracketMultiplier;
		}
		return 0;
	}

	protected Integer totalPoints(List<Points> pointsList) {
		return pointsList.stream().map(p -> p.getAmount()).reduce(0, Integer::sum);
	}
}
