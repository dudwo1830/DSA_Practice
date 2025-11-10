package study01_account_book.service;

import java.lang.reflect.Field;
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

		filtered.forEach(System.out::println);
		// TODO 🔥 여기서부터 네가 작성할 부분
		// 1) 전체 합계
		int monthlyTotal = filtered.stream().mapToInt(x -> x.getAmount()).sum();
		System.out.println(monthlyTotal);

		// 2) 카테고리별 합계 (groupingBy(Category, summingInt(Transaction::getAmount)))
		Map<Category, Integer> sumByCategory = filtered.stream()
				.collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingInt(Transaction::getAmount)));
		;
		// TODO 출력 형태 꾸미기
		System.out.println(sumByCategory);

		// 3) 내림차순 정렬
		filtered.stream().sorted((x, y) -> {
			return y.getId() - x.getId();
		}).forEach(System.out::println);

		// 4) % 비율 계산

		// 5) 보기 좋은 출력 형식 구성
	}

	public void saveToCsv(String path) {
		// TODO CSV 저장
	}

	public void loadFromCsv(String path) {
		// TODO CSV 불러오기
	}
}