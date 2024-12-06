import java.util.Objects;

public class Media {
    private String title;
    private int releaseYear;
    private String category;
    private float rating;

    public Media(String title, int releaseYear, String category, float rating) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.category = category;
        this.rating = rating;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return title + "; " + releaseYear + "; " + category +"; " + rating;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Media media = (Media) obj;
        return Objects.equals(title, media.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
