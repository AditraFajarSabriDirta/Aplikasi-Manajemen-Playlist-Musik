import java.io.File;

public class LocalMusic extends Music {
    // Atribut tambahan khusus untuk local music
    private long fileSize; // ukuran file dalam bytes

    // Constructor
    public LocalMusic(String title, String artist, int duration, String genre, String filePath) {
        // Memanggil constructor parent class
        super(title, artist, duration, genre, filePath);
        this.fileSize = getFileSizeFromPath(filePath);
    }

    // Method untuk mendapatkan ukuran file
    private long getFileSizeFromPath(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return file.length();
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    // Getter untuk file size
    public long getFileSize() {
        return fileSize;
    }

    // Method untuk format file size ke KB atau MB
    public String getFormattedFileSize() {
        if (fileSize < 1024 * 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    // POLIMORFISME: Override method dari parent class
    @Override
    public String getMusicInfo() {
        return "🎵 " + getTitle() + " - " + getArtist() +
                " [" + getFormattedDuration() + "] (" + getGenre() + ")";
    }

    // Method khusus untuk mengecek apakah file ada
    public boolean fileExists() {
        File file = new File(getFilePath());
        return file.exists();
    }

    // Method untuk mendapatkan detail lengkap
    public String getDetailedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Title: ").append(getTitle()).append("\n");
        info.append("Artist: ").append(getArtist()).append("\n");
        info.append("Duration: ").append(getFormattedDuration()).append("\n");
        info.append("Genre: ").append(getGenre()).append("\n");
        info.append("File Size: ").append(getFormattedFileSize()).append("\n");
        info.append("Path: ").append(getFilePath());
        return info.toString();
    }
}