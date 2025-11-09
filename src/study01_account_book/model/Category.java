package study01_account_book.model;

public enum Category {
    FOOD("식비"),
    TRANSPORT("교통"),
    SHOPPING("쇼핑"),
    OTHER("기타");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Category fromLabel(String input) {
        for (Category c : values()) {
            if (c.label.equalsIgnoreCase(input) || c.name().equalsIgnoreCase(input)) {
                return c;
            }
        }
        return OTHER; // fallback
    }
}