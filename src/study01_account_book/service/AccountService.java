package study01_account_book.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import study01_account_book.model.Category;
import study01_account_book.model.Transaction;
import study01_account_book.repository.TransactionRepository;

public class AccountService {

	private TransactionRepository repo = new TransactionRepository();

	public void addTransaction(String dateStr, String categoryStr, String desc, int amount) {
		try {
			LocalDate date = LocalDate.parse(dateStr);
			Category category = Category.fromLabel(categoryStr);
			int id = repo.createNewId();
			repo.add(new Transaction(id, date, category, desc, amount));
		} catch (DateTimeParseException e) {
			System.out.println("날짜 형식 오류 (예: 2025-11-07)");
		}
	}

	public void showAll() {
		List<Transaction> list = repo.findAll();
		if (list.isEmpty()) {
			System.out.println("기록 없음.");
			return;
		}
		list.forEach(System.out::println);
	}

	public void delete(int id) {
		repo.deleteById(id);
	}

	public void showMonthlySummary(int year, int month) {
		List<Transaction> filtered = repo.findAll().stream()
				.filter(t -> t.getDate().getYear() == year && t.getDate().getMonthValue() == month)
				.toList();

		// TODO 🔥 여기서부터 네가 작성할 부분
		// 1) 전체 합계
		int monthlyTotal = getMonthlyTotal(filtered);
		// 2) 카테고리별 합계 (groupingBy(Category, summingInt(Transaction::getAmount)))
		Map<Category, Integer> sumByCategory = filtered.stream()
				.collect(Collectors.groupingBy(
						Transaction::getCategory, Collectors.summingInt(Transaction::getAmount)));

		// 3) Amount 기준 내림차순 정렬
		List<Map.Entry<Category, Integer>> sortedResult = sumByCategory.entrySet().stream()
				.sorted(Map.Entry.<Category, Integer>comparingByValue().reversed())
				.toList();

		// 4) 합계 대비 % 비율 계산
		// 5) 보기 좋은 출력 형식 구성
		// monthlyTotal: totalAmount
		// category1 | amount (%)
		// category2 | amount (%)
		// ...
		System.out.println("월 합계: " + String.format("%,d원", monthlyTotal));
		sortedResult.stream().forEach(data -> {
			double rate = (data.getValue() * 1.0 / monthlyTotal) * 100;
			String msg = data.getKey().getLabel() + " | "
					+ String.format("%,d원", data.getValue()) + " | "
					+ Math.round(rate) + "%";
			System.out.println(msg);
		});
	}

	public void saveToCsv(String path) {
		
		// TODO CSV 저장
		try {
			Files.writeString(Path.of(path), repo.transactionToStringForCsv());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadFromCsv(String path) {
		// TODO CSV 불러오기
	}

	private int getMonthlyTotal(List<Transaction> list) {
		return list.stream().mapToInt(x -> x.getAmount()).sum();
	}
}