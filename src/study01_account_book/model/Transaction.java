package study01_account_book.model;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private LocalDate date;
    private Category category;
    private String description;
    private int amount;

    public Transaction(int id, LocalDate date, Category category, String description, int amount) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public Category getCategory() { return category; }
    public String getDescription() { return description; }
    public int getAmount() { return amount; }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | %s | %d원",
                id, date, category.getLabel(), description, amount);
    }
}