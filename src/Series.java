public class Series extends Media {

    private int season;
    private int episode;

    public Series(String title, int releaseYear, String category, float rating, int season, int episode) {
        super(title, releaseYear, category, rating);
        this.season = season;
        this.episode = episode;
    }

    public int getSeason() {
        return season;
    }

    public int getEpisode() {
        return episode;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }
}
