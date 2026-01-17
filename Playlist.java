import java.io.*;
import java.util.ArrayList;

public class Playlist {
    private ArrayList<Music> musicList;
    private String playlistName;

    // Constructor
    public Playlist(String name) {
        this.playlistName = name;
        this.musicList = new ArrayList<>();
    }

    // Tambah musik ke playlist
    public boolean addMusic(Music music) {
        if (music != null) {
            musicList.add(music);
            return true;
        }
        return false;
    }

    // Hapus musik dari playlist berdasarkan index
    public boolean removeMusic(int index) {
        if (index >= 0 && index < musicList.size()) {
            musicList.remove(index);
            return true;
        }
        return false;
    }

    // Update musik pada index tertentu
    public boolean updateMusic(int index, Music newMusic) {
        if (index >= 0 && index < musicList.size() && newMusic != null) {
            musicList.set(index, newMusic);
            return true;
        }
        return false;
    }

    // Dapatkan musik berdasarkan index
    public Music getMusic(int index) {
        if (index >= 0 && index < musicList.size()) {
            return musicList.get(index);
        }
        return null;
    }

    // Dapatkan semua musik
    public ArrayList<Music> getAllMusic() {
        return new ArrayList<>(musicList);
    }

    // Dapatkan jumlah musik
    public int getMusicCount() {
        return musicList.size();
    }

    // Hitung total durasi playlist (dalam detik)
    public int getTotalDuration() {
        int total = 0;
        for (Music music : musicList) {
            total += music.getDuration();
        }
        return total;
    }

    // Format total durasi ke jam:menit:detik
    public String getFormattedTotalDuration() {
        int total = getTotalDuration();
        int hours = total / 3600;
        int minutes = (total % 3600) / 60;
        int seconds = total % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    // Cari musik berdasarkan judul
    public ArrayList<Music> searchByTitle(String keyword) {
        ArrayList<Music> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Music music : musicList) {
            if (music.getTitle().toLowerCase().contains(lowerKeyword)) {
                results.add(music);
            }
        }
        return results;
    }

    // Cari musik berdasarkan artis
    public ArrayList<Music> searchByArtist(String keyword) {
        ArrayList<Music> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Music music : musicList) {
            if (music.getArtist().toLowerCase().contains(lowerKeyword)) {
                results.add(music);
            }
        }
        return results;
    }

    // Simpan playlist ke file
    public boolean saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Tulis nama playlist
            writer.write("PLAYLIST:" + playlistName);
            writer.newLine();

            // Tulis setiap musik
            for (Music music : musicList) {
                writer.write(music.toFileString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Load playlist dari file
    public boolean loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            musicList.clear();

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine && line.startsWith("PLAYLIST:")) {
                    playlistName = line.substring(9);
                    firstLine = false;
                    continue;
                }

                Music music = Music.fromFileString(line);
                if (music != null) {
                    musicList.add(music);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Getter dan Setter
    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    // Clear semua musik
    public void clear() {
        musicList.clear();
    }

    // Check apakah playlist kosong
    public boolean isEmpty() {
        return musicList.isEmpty();
    }
}