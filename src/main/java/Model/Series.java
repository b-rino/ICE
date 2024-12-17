package Model;

public class Series extends MediaItem {

    private int season;
    private int episode;

    public Series(String title, int releaseYear, String category, float rating, int id, int season, int episode) {
        super(title, releaseYear, category, rating, id);
        this.season = season;
        this.episode = episode;
    }

    public int getSeason() {
        return season;
    }

    public int getEpisode() {
        return episode;
    }

    @Override
    public String toString() {
        return super.toString() + " Season: " + season + " Episode: " + episode;
    }
}
