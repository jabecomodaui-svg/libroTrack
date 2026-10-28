package model;

public class Category {
    private int categoryId;
    private String categoryName;

    public Category() { }

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        setCategoryName(categoryName);   // use the setter so validation runs
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        this.categoryName = categoryName.trim();
    }

    @Override
    public String toString() { return categoryName; }   // combo boxes show this text
}
