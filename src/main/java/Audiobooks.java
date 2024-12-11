
public class Audiobooks extends MediaItem {
    private String author;

    public Audiobooks(String title, int releaseYear, String category, float rating, String author) {
        super(title, releaseYear, category, rating);
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return super.toString() + " Author: " + author;
    }
}
