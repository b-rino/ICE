package Model;

public class Audiobooks extends MediaItem {
    private String author;

    public Audiobooks(String title, int releaseYear, String category, float rating, int id, String author) {
        super(title, releaseYear, category, rating, id);
        this.author = author;
    }

    @Override
    public String toString() {
        return super.toString() + " Author: " + author;
    }
}
