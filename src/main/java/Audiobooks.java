
public class Audiobooks extends MediaItem {
    private String author;

    public Audiobooks(String title, String author, int releaseYear, String category, float rating) {
        super(title, releaseYear, category, rating);
        this.author = author;
    }


}
