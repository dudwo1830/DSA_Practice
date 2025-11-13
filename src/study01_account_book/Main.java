package study01_account_book;

import study01_account_book.service.AccountService;

public class Main {

	public static void main(String[] args) {
		AccountService service = new AccountService();
		service.addTransaction("2025-11-09", "교통", "집->중앙역", 1600);
		service.addTransaction("2025-11-09", "교통", "중앙->교대->집", 1800);
		service.addTransaction("2025-11-09", "식비", "햄버거", 7600);

		service.showMonthlySummary(2025, 11);
		service.saveToCsv("practice/src/study01_account_book/data/accountService.csv");
	}
}