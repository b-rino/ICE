package Model;

import java.util.Objects;

    public class MediaItem {
    private String title;
    private int releaseYear;
    private String category;
    private float rating;
    private int id;

    public MediaItem(String title, int releaseYear, String category, float rating, int id) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.category = category;
        this.rating = rating;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public float getRating() {
        return rating;
    }


    @Override
    public String toString() {
        return "Title: " + title + ", Year: " + releaseYear + ", Category: " + category + ", Rating: " + rating;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MediaItem media = (MediaItem) obj;
        return Objects.equals(title, media.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
