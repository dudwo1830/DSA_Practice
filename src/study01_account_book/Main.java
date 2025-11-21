package study01_account_book;

import java.util.Scanner;

import study01_account_book.service.AccountService;

public class Main {
	private static final String SAV_PATH = "./practice/src/study01_account_book";

	public static void main(String[] args) {
		AccountService service = new AccountService();

		Scanner sc = new Scanner(System.in);

		System.out.println("시작경로: " + System.getProperty("user.dir"));

		while (true) {
			System.out.println("\n------ 가계부 ------");
			System.out.println("1. 수입/지출 추가");
			System.out.println("2. 전체 목록 조회");
			System.out.println("3. 삭제");
			System.out.println("4. 월별 요약 보기");
			System.out.println("5. CSV 저장");
			System.out.println("6. CSV 불러오기");
			System.out.println("0. 종료");
			System.out.print("메뉴 선택: ");

			String menu = sc.nextLine();

			switch (menu) {
				case "1":
					System.out.print("날짜 (YYYY-MM-DD): ");
					String date = sc.nextLine();
					System.out.print("카테고리: ");
					String category = sc.nextLine();
					System.out.print("내용: ");
					String desc = sc.nextLine();
					System.out.print("금액 (+수입 / -지출): ");
					int amount = Integer.parseInt(sc.nextLine());
					service.addTransaction(date, category, desc, amount);
					break;

				case "2":
					service.showAll();
					break;

				case "3":
					System.out.print("삭제할 ID: ");
					int id = Integer.parseInt(sc.nextLine());
					service.delete(id);
					break;

				case "4":
					System.out.print("연도: ");
					int y = Integer.parseInt(sc.nextLine());
					System.out.print("월: ");
					int m = Integer.parseInt(sc.nextLine());
					service.showMonthlySummary(y, m);
					break;

				case "5":
					service.saveToCsv(SAV_PATH + "accountbook.csv");
					break;

				case "6":
					service.loadFromCsv(SAV_PATH + "accountbook.csv");
					break;

				case "0":
					sc.close();
					return;

				default:
					System.out.println("잘못된 입력");
			}
		}
	}
}