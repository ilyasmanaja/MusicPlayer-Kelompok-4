/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.musicplayer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Image;
import javax.swing.ImageIcon;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import java.io.File;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.mycompany.musicplayer.Song; 
import com.mycompany.musicplayer.MusicPlayerEngine;

/**
 *
 * @author Raihan
 */
public class MusicFrame extends JFrame implements PropertyChangeListener {
    private JPanel topBarPanel;
    private JPanel leftSidebarPanel;
    private JPanel songListPanel;
    private JPanel playerControlsPanel;
    
    private JTable songTable;
    private DefaultTableModel tableModel;
    private JButton playPauseButton;
    private JSlider songSlider;
    private JLabel timeStartLabel;
    private JLabel timeEndLabel;
    
    private ImageIcon iconPlay;
    private ImageIcon iconPause;
    private ImageIcon iconNext;
    private ImageIcon iconPrev;
    
    
    private MusicPlayerEngine playerEngine;
    private List<Song> songList;

    public MusicFrame() {
        // --- Setup Jendela Utama ---
        setTitle("Music Player");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        playerEngine = new MusicPlayerEngine();
        playerEngine.addPropertyChangeListener(this);
        songList = new ArrayList<>();
        
        loadAllIcons();
        
        topBarPanel = new JPanel();
        topBarPanel.setPreferredSize(new Dimension(getWidth(), 50));
        add(topBarPanel, BorderLayout.NORTH);
        
        leftSidebarPanel = new JPanel();
        leftSidebarPanel.setPreferredSize(new Dimension(180, getHeight()));
        add(leftSidebarPanel, BorderLayout.WEST);
        
        initSongListPanel();
        initPlayerControlsPanel();
        loadDummySongs();
    }
    
    private ImageIcon loadAndScaleIcon(String path, int width, int height) {
        try {
            Image originalImage = new ImageIcon(getClass().getResource(path)).getImage();
            
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            
            return new ImageIcon(scaledImage);
        }   catch(Exception e) {
            System.err.println("Gagal load icon: " + path);
            e.printStackTrace();
            return null;
        }
    }
    
    private void loadAllIcons() {
        int iconSize = 28;
        
        iconPlay = loadAndScaleIcon("/icons/play.png", iconSize, iconSize);
        iconPause = loadAndScaleIcon("/icons/pause.png", iconSize, iconSize);
        iconNext = loadAndScaleIcon("/icons/next.png", iconSize, iconSize);
        iconPrev = loadAndScaleIcon("/icons/prev.png", iconSize, iconSize);
    }
    
    private Song createSongFromMetadata(String filePath) {
        try {
            File file = new File(filePath);
            AudioFile audioFile = AudioFileIO.read(file);
            
            Tag tag = audioFile.getTag();
            int duration = audioFile.getAudioHeader().getTrackLength();
            
            String title = tag.getFirst(FieldKey.TITLE);
            String artist = tag.getFirst(FieldKey.ARTIST);
            String album = tag.getFirst(FieldKey.ALBUM);
            
            if (title == null || title.isEmpty()) {
                title = file.getName();
            }
            if (artist == null || artist.isEmpty()) {
                artist = "Unknown Artist";
            }
            if (album == null || album.isEmpty()) {
                album = "Unknown Album";
            }
            
            System.out.println(">>> jaudiotiger BERHASIL baca: " + title);
            
            return new Song(title, artist, album, duration, filePath);      
        }   catch (Exception e) {
            System.err.println("Error membaca metadata dari: " + filePath);
            e.printStackTrace();
            return null;
        }
    }
    
    private void initSongListPanel() {
        songListPanel = new JPanel(new BorderLayout());

        // Tentukan nama kolom untuk tabel
        String[] columnNames = {"Judul", "Artis", "Durasi"};

        // Buat table model, tapi set 0 baris awal
        // Kita juga buat agar tabelnya tidak bisa diedit
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        songTable = new JTable(tableModel);
        
        songTable.setRowHeight(30); 
        songTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        songTable.setShowGrid(false); 
        songTable.setIntercellSpacing(new Dimension(0, 0));
        songTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 

        JScrollPane scrollPane = new JScrollPane(songTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 

        songListPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(songListPanel, BorderLayout.CENTER);
    }

    private void initPlayerControlsPanel() {
        playerControlsPanel = new JPanel(new BorderLayout()); 
        playerControlsPanel.setPreferredSize(new Dimension(getWidth(), 90));

        
        JPanel buttonPanel = new JPanel(); 
        
        JButton prevButton = new JButton(iconPrev);
        playPauseButton = new JButton(iconPlay); 
        JButton nextButton = new JButton(iconNext);
        
        JButton[] buttons = {prevButton, playPauseButton, nextButton};
        for (JButton  btn : buttons) {
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        buttonPanel.add(prevButton);
        buttonPanel.add(playPauseButton);
        buttonPanel.add(nextButton);

        // --- Panel untuk Slider (Tengah) ---
        JPanel sliderPanel = new JPanel(new BorderLayout());
        songSlider = new JSlider();
        songSlider.setValue(0);
        
        songSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int newSeconds = songSlider.getValue();
                playerEngine.seek(newSeconds);
                
                timeStartLabel.setText(formatDuration(newSeconds));
            }
        });
        
        // Tambahkan label durasi (nanti)
        timeStartLabel = new JLabel("0:00");
        timeEndLabel = new JLabel("0:00");
        
        sliderPanel.add(songSlider, BorderLayout.CENTER);
        sliderPanel.add(timeStartLabel, BorderLayout.WEST);
        sliderPanel.add(timeEndLabel, BorderLayout.EAST);
        
        // --- Tambahkan panel-panel kecil ke panel kontrol utama ---
        playerControlsPanel.add(buttonPanel, BorderLayout.CENTER);
        playerControlsPanel.add(sliderPanel, BorderLayout.NORTH); // Slider di atas tombol
        
        // Tambahkan panel kontrol ke frame
        add(playerControlsPanel, BorderLayout.SOUTH);

        // --- TAMBAHKAN FUNGSI (ACTION LISTENER) ---
        playPauseButton.addActionListener(e -> {
            togglePlayPause(); // Panggil method helper
        });
    }

    /**
     * Mengisi tabel dengan data lagu bohongan (dummy).
     */
    private void loadDummySongs() {
        String path1 = "D:\\Music\\SoundHelix-Song-1.mp3";
        String path2 = "D:\\Music\\Multo.mp3";
        String path3 = "D:\\Music\\Lany.mp3";
        
        Song song1 = createSongFromMetadata(path1);
        Song song2 = createSongFromMetadata(path2);
        Song song3 = createSongFromMetadata(path3);
        
        if (song1 != null) {
            songList.add(song1);
        }
        if (song2 != null) {
            songList.add(song2);
        }
        if (song3 != null) {
            songList.add(song3);
        }
        
        for(Song song : songList) {
            Object[] rowData = {
                song.getTitle(),
                song.getArtist(),
                song.getFormattedDuration()
            };
            tableModel.addRow(rowData);
        }
    }

    /**
     * Logika untuk tombol Play/Pause.
     */
    private void togglePlayPause() {
        if (playerEngine.isPlaying()) {
            // --- Jika sedang main, PAUSE ---
            playerEngine.pause();
            playPauseButton.setIcon(iconPlay);
        } else {
            // --- Jika sedang pause atau stop, PLAY ---
            
            // 1. Dapatkan lagu yang dipilih dari tabel
            int selectedRow = songTable.getSelectedRow();
            
            // Cek apakah ada lagu yang dipilih
            if (selectedRow == -1) {
                // Jika tidak ada, mainkan lagu pertama di daftar
                selectedRow = 0;
                songTable.setRowSelectionInterval(0, 0);
            }

            // 2. Ambil objek Song dari daftar kita
            Song songToPlay = songList.get(selectedRow);

            // 3. Cek apakah lagu ini lagu yang baru
            //    atau hanya melanjutkan lagu yang di-pause
            if (songToPlay != playerEngine.getCurrentSong()) {
                playerEngine.loadSong(songToPlay);
            }

            // 4. Putar lagunya!
            playerEngine.play();
            playPauseButton.setIcon(iconPause); // Ubah ikon ke 'Pause'
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
       SwingUtilities.invokeLater(() ->{
           if("songReady".equals(evt.getPropertyName())) {
               double totalSeconds = (double) evt.getNewValue();
               
               songSlider.setMaximum((int) totalSeconds);
               
               timeEndLabel.setText(formatDuration((long) totalSeconds));
           }    else if ("timeUpdate".equals(evt.getPropertyName())) {
               double[] times = (double[]) evt.getNewValue();
               double currentSeconds = times[0];
               
               if(!songSlider.getValueIsAdjusting()) {
                   songSlider.setValue((int) currentSeconds);
               }
               timeStartLabel.setText(formatDuration((long) currentSeconds));
           }
       });
    }
    
    public String formatDuration(long durationInSeconds) {
        long minutes = durationInSeconds / 60;
        long seconds = durationInSeconds % 60;
        
        return String.format("%d:%02d", minutes, seconds);
    }
}
