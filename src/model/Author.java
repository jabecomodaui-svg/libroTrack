package model;

public class Author {
    private int authorId;
    private String authorName;

    public Author() { }

    public Author(int authorId, String authorName) {
        this.authorId = authorId;
        setAuthorName(authorName);
    }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) {
        if (authorName == null || authorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty.");
        }
        this.authorName = authorName.trim();
    }

    @Override
    public String toString() { return authorName; }
}
