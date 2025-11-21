package study01_account_book.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
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
		System.out.println("월간 수입");
		int monthlyIncome = closingResult(filtered, (x) -> x.getAmount() >= 0);
		System.out.println("------------------------------------------------------------------");
		System.out.println("월간 지출");
		int monthlyExpense = closingResult(filtered, (x) -> x.getAmount() < 0);

		System.out.println(year + "년" + month + "월 합계: " + (monthlyIncome + monthlyExpense));

	}

	public void saveToCsv(String pathStr) {
		Path path = Path.of(pathStr);
		// TODO CSV 저장
		try {
			// 폴더가 없을 경우 생성
			Files.createDirectories(path.getParent());
			// 파일 작성
			Files.writeString(path, repo.transactionToStringForCsv());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadFromCsv(String pathStr) {
		Path path = Path.of(pathStr);
		// TODO CSV 불러오기
		try {
			String content = Files.readString(path);
			repo.reset();
			String[] col = content.split("\n");
			for (String str : col) {
				String[] data = str.split(",");
				addTransaction(data[1], data[2], data[3], Integer.parseInt(data[4]));
			}
		} catch (IOException e) {
			// e.printStackTrace();
			System.err.println("-------------------------");
			System.err.println("파일을 찾을 수 없음");
			System.err.println(path.toString());
			System.err.println("-------------------------");
		}
	}

	private int closingResult(List<Transaction> list, Predicate<Transaction> filterPredicate) {
		// 1) 전체 합계
		int total = list.stream().filter(filterPredicate).mapToInt(x -> x.getAmount()).sum();
		// 2) 카테고리별 합계 (groupingBy(Category, summingInt(Transaction::getAmount)))
		Map<Category, Integer> sumTotalByCategory = list.stream()
				.filter(filterPredicate)
				.collect(Collectors.groupingBy(
						Transaction::getCategory, Collectors.summingInt(Transaction::getAmount)));

		// 3) Amount 기준 내림차순 정렬
		List<Map.Entry<Category, Integer>> sortedResult = sumTotalByCategory.entrySet().stream()
				.sorted(Map.Entry.<Category, Integer>comparingByValue().reversed())
				.toList();

		// 4) 합계 대비 % 비율 계산
		// 5) 보기 좋은 출력 형식 구성
		// monthlyTotal: totalAmount
		// category1 | amount (%)
		// category2 | amount (%)
		// ...
		System.out.println("합계: " + String.format("%,d원", total));
		sortedResult.stream().forEach(data -> {
			double rate = (data.getValue() * 1.0 / total) * 100;
			String msg = data.getKey().getLabel() + " | "
					+ String.format("%,d원", data.getValue()) + " | "
					+ Math.round(rate) + "%";
			System.out.println(msg);
		});

		return total;

	}

	// 수입
	private int printIncome(List<Transaction> list) {
		// 1) 전체 합계
		int income = list.stream().filter((x) -> x.getAmount() >= 0).mapToInt(x -> x.getAmount()).sum();
		// 2) 카테고리별 합계 (groupingBy(Category, summingInt(Transaction::getAmount)))
		Map<Category, Integer> sumExpenseByCategory = list.stream()
				.filter((x) -> x.getAmount() >= 0)
				.collect(Collectors.groupingBy(
						Transaction::getCategory, Collectors.summingInt(Transaction::getAmount)));

		// 3) Amount 기준 내림차순 정렬
		List<Map.Entry<Category, Integer>> sortedResult = sumExpenseByCategory.entrySet().stream()
				.sorted(Map.Entry.<Category, Integer>comparingByValue().reversed())
				.toList();

		// 4) 합계 대비 % 비율 계산
		// 5) 보기 좋은 출력 형식 구성
		// monthlyTotal: totalAmount
		// category1 | amount (%)
		// category2 | amount (%)
		// ...
		System.out.println("월간 소득: " + String.format("%,d원", income));
		sortedResult.stream().forEach(data -> {
			double rate = (data.getValue() * 1.0 / income) * 100;
			String msg = data.getKey().getLabel() + " | "
					+ String.format("%,d원", data.getValue()) + " | "
					+ Math.round(rate) + "%";
			System.out.println(msg);
		});

		return income;

	}

	// 지출
	private int printExpense(List<Transaction> list) {
		// 1) 전체 합계
		int expense = list.stream().filter((x) -> x.getAmount() < 0).mapToInt(x -> x.getAmount()).sum();
		// 2) 카테고리별 합계 (groupingBy(Category, summingInt(Transaction::getAmount)))
		Map<Category, Integer> sumExpenseByCategory = list.stream()
				.filter((x) -> x.getAmount() < 0)
				.collect(Collectors.groupingBy(
						Transaction::getCategory, Collectors.summingInt(Transaction::getAmount)));

		// 3) Amount 기준 내림차순 정렬
		List<Map.Entry<Category, Integer>> sortedResult = sumExpenseByCategory.entrySet().stream()
				.sorted(Map.Entry.<Category, Integer>comparingByValue().reversed())
				.toList();

		// 4) 합계 대비 % 비율 계산
		// 5) 보기 좋은 출력 형식 구성
		// monthlyTotal: totalAmount
		// category1 | amount (%)
		// category2 | amount (%)
		// ...
		System.out.println("월간 지출: " + String.format("%,d원", expense));
		sortedResult.stream().forEach(data -> {
			double rate = (data.getValue() * 1.0 / expense) * 100;
			String msg = data.getKey().getLabel() + " | "
					+ String.format("%,d원", data.getValue()) + " | "
					+ Math.round(rate) + "%";
			System.out.println(msg);
		});

		return expense;
	}
}