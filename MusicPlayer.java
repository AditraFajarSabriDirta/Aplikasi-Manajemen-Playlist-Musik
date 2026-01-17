import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private Clip clip;
    private AudioInputStream audioStream;
    private String currentFilePath;
    private long clipTimePosition;
    private boolean isPaused;

    // Constructor
    public MusicPlayer() {
        this.clip = null;
        this.audioStream = null;
        this.currentFilePath = null;
        this.clipTimePosition = 0;
        this.isPaused = false;
    }

    /**
     * Load dan putar file audio
     * @param filePath path ke file .wav
     * @return true jika berhasil, false jika gagal
     */
    public boolean play(String filePath) {
        try {
            // Stop musik sebelumnya jika ada
            stop();

            // Load file audio
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.err.println("File tidak ditemukan: " + filePath);
                return false;
            }

            audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Simpan path file saat ini
            currentFilePath = filePath;
            clipTimePosition = 0;
            isPaused = false;

            // Mulai putar
            clip.start();

            return true;
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Format audio tidak didukung: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error membaca file: " + e.getMessage());
            return false;
        } catch (LineUnavailableException e) {
            System.err.println("Audio line tidak tersedia: " + e.getMessage());
            return false;
        }
    }

    /**
     * Pause pemutaran musik
     */
    public void pause() {
        if (clip != null && clip.isRunning()) {
            clipTimePosition = clip.getMicrosecondPosition();
            clip.stop();
            isPaused = true;
        }
    }

    /**
     * Resume pemutaran musik yang di-pause
     */
    public void resume() {
        if (clip != null && isPaused) {
            clip.setMicrosecondPosition(clipTimePosition);
            clip.start();
            isPaused = false;
        }
    }

    /**
     * Stop pemutaran musik
     */
    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }

        if (audioStream != null) {
            try {
                audioStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            audioStream = null;
        }

        currentFilePath = null;
        clipTimePosition = 0;
        isPaused = false;
    }

    /**
     * Seek to specific position in microseconds
     */
    public void seek(long microseconds) {
        if (clip != null && microseconds >= 0 && microseconds <= getTotalDuration()) {
            boolean wasPlaying = clip.isRunning();
            clip.stop();
            clip.setMicrosecondPosition(microseconds);
            if (wasPlaying && !isPaused) {
                clip.start();
            } else if (isPaused) {
                clipTimePosition = microseconds;
            }
        }
    }

    /**
     * Check apakah sedang memutar musik
     */
    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    /**
     * Check apakah musik di-pause
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Dapatkan posisi saat ini dalam mikro detik
     */
    public long getCurrentPosition() {
        if (clip != null) {
            return clip.getMicrosecondPosition();
        }
        return 0;
    }

    /**
     * Dapatkan total durasi dalam mikro detik
     */
    public long getTotalDuration() {
        if (clip != null) {
            return clip.getMicrosecondLength();
        }
        return 0;
    }

    /**
     * Dapatkan posisi saat ini dalam format string mm:ss
     */
    public String getCurrentPositionString() {
        long microseconds = getCurrentPosition();
        int seconds = (int) (microseconds / 1_000_000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Set volume (0.0 - 1.0)
     */
    public void setVolume(float volume) {
        if (clip != null) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float range = max - min;
                float gain = min + (range * volume);
                volumeControl.setValue(gain);
            } catch (Exception e) {
                System.err.println("Volume control tidak tersedia");
            }
        }
    }

    /**
     * Dapatkan file path yang sedang diputar
     */
    public String getCurrentFilePath() {
        return currentFilePath;
    }

    /**
     * Check apakah musik sudah selesai
     */
    public boolean isFinished() {
        if (clip != null) {
            return getCurrentPosition() >= getTotalDuration();
        }
        return true;
    }

    /**
     * Get clip (for external access if needed)
     */
    public Clip getClip() {
        return clip;
    }
}