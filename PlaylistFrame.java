import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.border.*;
import javax.sound.sampled.*;
import javax.imageio.ImageIO;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlaylistFrame extends JFrame {
    private Playlist playlist;
    private MusicPlayer musicPlayer;
    private JTable musicTable;
    private DefaultTableModel tableModel;
    private JButton playButton, pauseButton, stopButton;
    private JProgressBar progressBar;
    private JLabel statusLabel, totalDurationLabel;
    private Timer progressTimer;
    private int currentPlayingIndex = -1;

    // Custom Colors untuk gradient theme
    private final Color DARK_BG = new Color(30, 30, 40);
    private final Color GRADIENT_START = new Color(45, 180, 120);
    private final Color GRADIENT_END = new Color(100, 100, 220);
    private final Color BUTTON_GREEN = new Color(253, 253, 253, 255);
    private final Color BUTTON_PURPLE = new Color(255, 255, 255, 255);
    private final Color BUTTON_RED = new Color(245, 245, 246);
    private final Color BUTTON_BLUE = new Color(99, 99, 218);
    private final Color TABLE_BG = new Color(50, 55, 70, 180);
    private final Color TABLE_FG = new Color(220, 220, 230);
    private final Color CONTROL_BG = new Color(50, 55, 70, 180);

    public PlaylistFrame() {
        // Initialize data
        playlist = new Playlist("Aplikasi Manajemen Playlist Musik");
        musicPlayer = new MusicPlayer();

        // Setup frame
        setTitle("Aplikasi Manajemen Playlist Musik");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set custom background panel with gradient
        setContentPane(new GradientPanel());
        setLayout(new BorderLayout(15, 15));

        // Initialize components
        initializeComponents();
        setupProgressTimer();

        // Add sample data
        addSampleData();
    }

    /**
     * Custom JPanel with background image
     */
    class GradientPanel extends JPanel {
        private Image backgroundImage;

        public GradientPanel() {
            try {
                // Try to load background.png from current directory
                File bgFile = new File("background.png");
                if (bgFile.exists()) {
                    backgroundImage = ImageIO.read(bgFile);
                }
            } catch (Exception e) {
                System.err.println("Could not load background image: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (backgroundImage != null) {
                // Draw background image scaled to fit
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Fallback to gradient if image not found
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(20, 25, 35),
                        getWidth(), getHeight(), new Color(40, 30, 50)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Add subtle pattern overlay
                g2d.setColor(new Color(255, 255, 255, 5));
                for (int i = 0; i < getHeight(); i += 4) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        }
    }

    /**
     * Custom rounded button
     */
    class RoundedButton extends JButton {
        private Color bgColor;
        private Color hoverColor;

        public RoundedButton(String text, Color bgColor) {
            super(text);
            this.bgColor = bgColor;
            this.hoverColor = bgColor.brighter();

            setForeground(Color.BLACK);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background with hover effect
            if (getModel().isRollover()) {
                g2d.setColor(hoverColor);
            } else {
                g2d.setColor(bgColor);
            }

            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

            // Add subtle shadow
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);

            super.paintComponent(g);
        }
    }

    /**
     * Custom rounded panel
     */
    class RoundedPanel extends JPanel {
        private Color backgroundColor;

        public RoundedPanel(Color bgColor) {
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(backgroundColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

            super.paintComponent(g);
        }
    }

    private void initializeComponents() {
        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout(20, 20));
        mainContainer.setOpaque(false);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // Center Panel with table and buttons
        JPanel centerPanel = new JPanel(new BorderLayout(15, 0));
        centerPanel.setOpaque(false);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        // Right button panel
        JPanel buttonPanel = createButtonPanel();
        centerPanel.add(buttonPanel, BorderLayout.EAST);

        mainContainer.add(centerPanel, BorderLayout.CENTER);

        // Control Panel (bottom)
        JPanel controlPanel = createControlPanel();
        mainContainer.add(controlPanel, BorderLayout.SOUTH);

        add(mainContainer);
    }

    private JPanel createHeaderPanel() {
        RoundedPanel headerPanel = new RoundedPanel(new Color(50, 55, 70, 180));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Title with icon
        JLabel titleLabel = new JLabel("Aplikasi Manajemen Playlist Musik");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        RoundedPanel tablePanel = new RoundedPanel(TABLE_BG);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create table model
        String[] columnNames = {"Title", "Artist", "Duration", "Genre"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        musicTable = new JTable(tableModel);
        musicTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        musicTable.setRowHeight(35);
        musicTable.setOpaque(false);
        musicTable.setBackground(new Color(0, 0, 0, 0));
        musicTable.setForeground(TABLE_FG);
        musicTable.setSelectionBackground(new Color(100, 120, 180));
        musicTable.setSelectionForeground(Color.WHITE);
        musicTable.setGridColor(new Color(70, 75, 90, 80));
        musicTable.setShowGrid(true);
        musicTable.setIntercellSpacing(new Dimension(1, 1));

        musicTable.setShowHorizontalLines(true);
        musicTable.setShowVerticalLines(true);

        // Custom header
        JTableHeader header = musicTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(40, 45, 60));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Column widths
        musicTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        musicTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        musicTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        musicTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        // Double click to play
        musicTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = musicTable.getSelectedRow();
                    if (row >= 0) {
                        playMusicAtIndex(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(musicTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Create buttons with custom styling
        RoundedButton addButton = new RoundedButton("Add Music", BUTTON_GREEN);
        RoundedButton editButton = new RoundedButton("Edit Music", BUTTON_PURPLE);
        RoundedButton deleteButton = new RoundedButton("Delete", BUTTON_RED);
        RoundedButton refreshButton = new RoundedButton("Refresh WAV", new Color(255, 255, 255));
        RoundedButton saveButton = new RoundedButton("Save Playlist", BUTTON_BLUE);
        RoundedButton loadButton = new RoundedButton("Load Playlist", BUTTON_BLUE);

        Dimension buttonSize = new Dimension(200, 50);
        addButton.setPreferredSize(buttonSize);
        addButton.setMaximumSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        editButton.setMaximumSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        deleteButton.setMaximumSize(buttonSize);
        refreshButton.setPreferredSize(buttonSize);
        refreshButton.setMaximumSize(buttonSize);
        saveButton.setPreferredSize(buttonSize);
        saveButton.setMaximumSize(buttonSize);
        loadButton.setPreferredSize(buttonSize);
        loadButton.setMaximumSize(buttonSize);

        // Add action listeners
        addButton.addActionListener(e -> addMusic());
        editButton.addActionListener(e -> editMusic());
        deleteButton.addActionListener(e -> deleteMusic());
        refreshButton.addActionListener(e -> refreshWAVFiles());
        saveButton.addActionListener(e -> autoSavePlaylist());
        loadButton.addActionListener(e -> loadPlaylist());

        // Add buttons with spacing
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(refreshButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(loadButton);

        return buttonPanel;
    }

    /**
     * Refresh WAV files from current directory
     */
    private void refreshWAVFiles() {
        playlist.clear();
        addSampleData();
        JOptionPane.showMessageDialog(this,
                "WAV files reloaded from folder!",
                "Refresh Complete",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createControlPanel() {
        RoundedPanel controlPanel = new RoundedPanel(CONTROL_BG);
        controlPanel.setLayout(new BorderLayout(15, 0));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Left side - Control buttons only
        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonGroup.setOpaque(false);

        playButton = createCircleButton("▶", new Color(130, 255, 180));
        pauseButton = createCircleButton("⏸", new Color(205, 205, 15));
        stopButton = createCircleButton("⏹", new Color(200, 200, 210));

        playButton.addActionListener(e -> playMusic());
        pauseButton.addActionListener(e -> pauseMusic());
        stopButton.addActionListener(e -> stopMusic());

        buttonGroup.add(playButton);
        buttonGroup.add(pauseButton);
        buttonGroup.add(stopButton);

        controlPanel.add(buttonGroup, BorderLayout.WEST);

        // Center - Progress bar and status
        JPanel centerGroup = new JPanel(new BorderLayout(10, 5));
        centerGroup.setOpaque(false);

        statusLabel = new JLabel("No music playing");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(180, 180, 190));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(400, 12));
        progressBar.setBackground(new Color(60, 65, 80));
        progressBar.setForeground(GRADIENT_START);
        progressBar.setBorderPainted(false);
        progressBar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Make progress bar clickable for seeking
        progressBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (musicPlayer.isPlaying() || musicPlayer.isPaused()) {
                    int mouseX = e.getX();
                    int width = progressBar.getWidth();
                    double clickPosition = (double) mouseX / width;

                    long totalDuration = musicPlayer.getTotalDuration();
                    long newPosition = (long) (totalDuration * clickPosition);

                    seekToPosition(newPosition);
                }
            }
        });

        centerGroup.add(statusLabel, BorderLayout.NORTH);
        centerGroup.add(progressBar, BorderLayout.CENTER);

        controlPanel.add(centerGroup, BorderLayout.CENTER);

        return controlPanel;
    }

    /**
     * Seek to specific position in microseconds
     */
    private void seekToPosition(long microseconds) {
        if (musicPlayer.isPlaying() || musicPlayer.isPaused()) {
            musicPlayer.seek(microseconds);
        }
    }

    private JButton createCircleButton(String text, Color color) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw circle background
                if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                g2d.fillOval(2, 2, getWidth()-4, getHeight()-4);

                // Draw icon
                g2d.setColor(Color.BLACK);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                if (text.equals("▶")) {
                    // Play triangle
                    int[] xPoints = {centerX - 6, centerX - 6, centerX + 8};
                    int[] yPoints = {centerY - 10, centerY + 10, centerY};
                    g2d.fillPolygon(xPoints, yPoints, 3);
                } else if (text.equals("⏸")) {
                    // Pause bars
                    g2d.fillRect(centerX - 8, centerY - 10, 5, 20);
                    g2d.fillRect(centerX + 3, centerY - 10, 5, 20);
                } else if (text.equals("⏹")) {
                    // Stop square
                    g2d.fillRect(centerX - 8, centerY - 8, 16, 16);
                }
            }
        };

        button.setPreferredSize(new Dimension(50, 50));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void setupProgressTimer() {
        progressTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (musicPlayer.isPlaying()) {
                    long current = musicPlayer.getCurrentPosition();
                    long total = musicPlayer.getTotalDuration();

                    if (total > 0) {
                        int progress = (int) ((current * 100) / total);
                        progressBar.setValue(progress);
                    }

                    if (musicPlayer.isFinished()) {
                        playNextSong();
                    }
                }
            }
        });
        progressTimer.start();
    }

    private void addSampleData() {
        // Auto-detect WAV files in the current directory
        File currentDir = new File(".");
        File[] files = currentDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

        if (files != null && files.length > 0) {
            for (File file : files) {
                try {
                    // Extract filename without extension as title
                    String filename = file.getName();
                    String title = filename.substring(0, filename.lastIndexOf('.'));

                    // Get file duration if possible
                    int duration = getAudioDuration(file);

                    // Create music entry with filename as title
                    LocalMusic music = new LocalMusic(
                            title,
                            "Unknown Artist",
                            duration,
                            "Unknown",
                            file.getAbsolutePath()
                    );

                    playlist.addMusic(music);
                } catch (Exception e) {
                    System.err.println("Error loading file: " + file.getName());
                }
            }

            refreshTable();
        }
    }

    /**
     * Get audio duration from WAV file
     */
    private int getAudioDuration(File file) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioStream.getFormat();
            long frames = audioStream.getFrameLength();
            double durationInSeconds = (frames + 0.0) / format.getFrameRate();
            audioStream.close();
            return (int) durationInSeconds;
        } catch (Exception e) {
            return 0;
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (int i = 0; i < playlist.getMusicCount(); i++) {
            Music music = playlist.getMusic(i);
            tableModel.addRow(new Object[]{
                    music.getTitle(),
                    music.getArtist(),
                    music.getFormattedDuration(),
                    music.getGenre()
            });
        }
    }

    private void addMusic() {
        JTextField titleField = new JTextField();
        JTextField artistField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField genreField = new JTextField();

        Object[] message = {
                "Title:", titleField,
                "Artist:", artistField,
                "Duration (seconds):", durationField,
                "Genre:", genreField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Music",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("WAV Files", "wav"));

                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    LocalMusic music = new LocalMusic(
                            titleField.getText(),
                            artistField.getText(),
                            Integer.parseInt(durationField.getText()),
                            genreField.getText(),
                            file.getAbsolutePath()
                    );

                    playlist.addMusic(music);
                    refreshTable();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid duration!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editMusic() {
        int selectedRow = musicTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a music to edit!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Music music = playlist.getMusic(selectedRow);

        JTextField titleField = new JTextField(music.getTitle());
        JTextField artistField = new JTextField(music.getArtist());
        JTextField durationField = new JTextField(String.valueOf(music.getDuration()));
        JTextField genreField = new JTextField(music.getGenre());

        Object[] message = {
                "Title:", titleField,
                "Artist:", artistField,
                "Duration (seconds):", durationField,
                "Genre:", genreField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Music",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                music.setTitle(titleField.getText());
                music.setArtist(artistField.getText());
                music.setDuration(Integer.parseInt(durationField.getText()));
                music.setGenre(genreField.getText());

                refreshTable();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid duration!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteMusic() {
        int selectedRow = musicTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a music to delete!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this music?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            playlist.removeMusic(selectedRow);
            refreshTable();
        }
    }

    /**
     * AUTO SAVE PLAYLIST - Simpan otomatis dengan nama bertimestamp
     */
    private void autoSavePlaylist() {
        try {
            // Buat nama file dengan timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timestamp = dateFormat.format(new Date());
            String filename = "MyPlaylist_" + timestamp + ".txt";

            // Simpan di folder yang sama dengan program
            File saveFile = new File(filename);

            if (playlist.saveToFile(saveFile.getAbsolutePath())) {
                // Tampilkan pesan sukses dengan lokasi file
                String message = "Playlist berhasil disimpan!\n\n" +
                        "Nama File: " + filename + "\n" +
                        "Lokasi: " + saveFile.getAbsolutePath() + "\n" +
                        "Total Lagu: " + playlist.getMusicCount() + "\n" +
                        "Total Durasi: " + playlist.getFormattedTotalDuration();

                JOptionPane.showMessageDialog(this,
                        message,
                        "Save Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Print ke console untuk konfirmasi
                System.out.println("Playlist saved: " + saveFile.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menyimpan playlist!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saat menyimpan: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * AUTO LOAD PLAYLIST - Load otomatis dengan menampilkan daftar file tersimpan
     */
    private void loadPlaylist() {
        try {
            // Cari semua file playlist di folder saat ini
            File currentDir = new File(".");
            File[] playlistFiles = currentDir.listFiles((dir, name) ->
                    name.startsWith("MyPlaylist_") && name.endsWith(".txt")
            );

            if (playlistFiles == null || playlistFiles.length == 0) {
                JOptionPane.showMessageDialog(this,
                        "Tidak ada file playlist yang tersimpan!\n\n" +
                                "Silakan save playlist terlebih dahulu menggunakan tombol 'Save Playlist'.",
                        "No Playlist Found",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Sort files by last modified (terbaru dulu)
            java.util.Arrays.sort(playlistFiles, (f1, f2) ->
                    Long.compare(f2.lastModified(), f1.lastModified())
            );

            // Buat array nama file untuk ditampilkan
            String[] fileNames = new String[playlistFiles.length];
            for (int i = 0; i < playlistFiles.length; i++) {
                // Format: nama file + ukuran + tanggal
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateStr = sdf.format(new java.util.Date(playlistFiles[i].lastModified()));
                long fileSizeKB = playlistFiles[i].length() / 1024;

                fileNames[i] = String.format("%s  [%d KB] - %s",
                        playlistFiles[i].getName(),
                        fileSizeKB,
                        dateStr
                );
            }

            // Tampilkan dialog pilihan dengan list
            JList<String> list = new JList<>(fileNames);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setSelectedIndex(0); // Pilih file terbaru
            list.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            list.setVisibleRowCount(10);

            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.setPreferredSize(new Dimension(600, 250));

            Object[] message = {
                    "Pilih playlist yang ingin di-load:",
                    scrollPane,
                    " ",
                    "Tip: File paling atas adalah yang terbaru"
            };

            int option = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Load Playlist - Select File",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option == JOptionPane.OK_OPTION) {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex >= 0) {
                    File selectedFile = playlistFiles[selectedIndex];

                    // Load playlist
                    if (playlist.loadFromFile(selectedFile.getAbsolutePath())) {
                        refreshTable();

                        // Hitung info playlist
                        int songCount = playlist.getMusicCount();
                        String totalDuration = playlist.getFormattedTotalDuration();

                        String successMessage = "Playlist berhasil di-load!\n\n" +
                                "File: " + selectedFile.getName() + "\n" +
                                "Total Lagu: " + songCount + "\n" +
                                "Total Durasi: " + totalDuration + "\n" +
                                "Lokasi: " + selectedFile.getAbsolutePath();

                        JOptionPane.showMessageDialog(this,
                                successMessage,
                                "Load Success",
                                JOptionPane.INFORMATION_MESSAGE);

                        System.out.println("Playlist loaded: " + selectedFile.getName());
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Gagal load playlist!\n\nFile mungkin rusak atau format tidak valid.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "⚠Silakan pilih file playlist terlebih dahulu!",
                            "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saat load playlist: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void playMusicAtIndex(int index) {
        if (index < 0 || index >= playlist.getMusicCount()) {
            return;
        }

        Music music = playlist.getMusic(index);
        if (music instanceof LocalMusic) {
            LocalMusic localMusic = (LocalMusic) music;

            if (musicPlayer.play(localMusic.getFilePath())) {
                currentPlayingIndex = index;
                statusLabel.setText("♪ " + music.getTitle() + " - " + music.getArtist());
                musicTable.setRowSelectionInterval(index, index);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to play music! Make sure the file exists.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void playMusic() {
        if (musicPlayer.isPaused()) {
            musicPlayer.resume();
        } else {
            int selectedRow = musicTable.getSelectedRow();
            if (selectedRow >= 0) {
                playMusicAtIndex(selectedRow);
            } else if (playlist.getMusicCount() > 0) {
                playMusicAtIndex(0);
            }
        }
    }

    private void pauseMusic() {
        musicPlayer.pause();
    }

    private void stopMusic() {
        musicPlayer.stop();
        progressBar.setValue(0);
        statusLabel.setText("No music playing");
        currentPlayingIndex = -1;
    }

    private void playNextSong() {
        if (currentPlayingIndex >= 0 && currentPlayingIndex < playlist.getMusicCount() - 1) {
            playMusicAtIndex(currentPlayingIndex + 1);
        } else {
            stopMusic();
        }
    }
}

