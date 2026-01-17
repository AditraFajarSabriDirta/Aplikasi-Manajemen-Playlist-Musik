import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set look and feel untuk tampilan yang lebih modern
        try {
            // Gunakan sistem look and feel untuk performa terbaik
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Tidak dapat mengatur look and feel: " + e.getMessage());
        }

        // Jalankan aplikasi di Event Dispatch Thread (EDT)
        // Ini adalah best practice untuk aplikasi Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Buat dan tampilkan frame utama
                PlaylistFrame frame = new PlaylistFrame();
                frame.setVisible(true);

                // Tampilkan pesan welcome
                showWelcomeMessage();
            }
        });
    }

    /**
     * Method untuk menampilkan pesan welcome dan instruksi
     */
    private static void showWelcomeMessage() {
        String message = "Selamat datang di Aplikasi Manajemen Playlist Musik\n";

        JOptionPane.showMessageDialog(null, message,
                "Aplikasi Manajemen Playlist Musik",
                JOptionPane.INFORMATION_MESSAGE);
    }
}