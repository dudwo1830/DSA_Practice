package study01_account_book.repository;

import study01_account_book.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
	private List<Transaction> list = new ArrayList<>();
	private int nextId = 1;

	public int createNewId() {
		return nextId++;
	}

	public void add(Transaction t) {
		list.add(t);
	}

	public List<Transaction> findAll() {
		return list;
	}

	public void deleteById(int id) {
		list.removeIf(t -> t.getId() == id);
	}

	
}