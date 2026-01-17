public class Music {

    private String title;
    private String artist;
    private int duration; // dalam detik
    private String genre;
    private String filePath;

    // Constructor default
    public Music() {
        this.title = "";
        this.artist = "";
        this.duration = 0;
        this.genre = "";
        this.filePath = "";
    }

    // Constructor dengan parameter
    public Music(String title, String artist, int duration, String genre, String filePath) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.genre = genre;
        this.filePath = filePath;
    }

    // GETTER dan SETTER untuk ENKAPSULASI
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // Method untuk format durasi dari detik ke menit:detik
    public String getFormattedDuration() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    // Method untuk mendapatkan info musik
    // Method ini akan di-override di child class (POLIMORFISME)
    public String getMusicInfo() {
        return title + " - " + artist + " [" + getFormattedDuration() + "]";
    }

    // Method untuk konversi ke string untuk disimpan ke file
    public String toFileString() {
        return title + "|" + artist + "|" + duration + "|" + genre + "|" + filePath;
    }

    // Method untuk parsing dari string file
    public static Music fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length == 5) {
            return new LocalMusic(parts[0], parts[1],
                    Integer.parseInt(parts[2]),
                    parts[3], parts[4]);
        }
        return null;
    }

    @Override
    public String toString() {
        return getMusicInfo();
    }
}