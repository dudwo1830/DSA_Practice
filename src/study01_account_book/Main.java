package study01_account_book;

import study01_account_book.service.AccountService;

public class Main {

	public static void main(String[] args) {
		AccountService service = new AccountService();

		service.loadFromCsv("accountService.csv");

		service.showMonthlySummary(2025, 11);
	}
}