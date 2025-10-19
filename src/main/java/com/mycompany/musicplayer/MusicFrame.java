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
    private JSlider volumeSlider;
    
    private ImageIcon iconPlay;
    private ImageIcon iconPause;
    private ImageIcon iconNext;
    private ImageIcon iconPrev;
    private ImageIcon iconVolume;
    
    
    private MusicPlayerEngine playerEngine;
    private List<Song> songList;

    public MusicFrame() {
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
        iconVolume = loadAndScaleIcon("/icons/volume.png", iconSize, iconSize);
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

        String[] columnNames = {"Judul", "Artis", "Durasi"};

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
        
        playerControlsPanel.add(buttonPanel, BorderLayout.CENTER);

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
        
        timeStartLabel = new JLabel("0:00");
        timeEndLabel = new JLabel("0:00");
        
        sliderPanel.add(songSlider, BorderLayout.CENTER);
        sliderPanel.add(timeStartLabel, BorderLayout.WEST);
        sliderPanel.add(timeEndLabel, BorderLayout.EAST);
        playerControlsPanel.add(sliderPanel, BorderLayout.NORTH);
        
        JPanel volumePanel = new JPanel (new FlowLayout());
        volumePanel.setPreferredSize(new Dimension(150, 90));
        
        JLabel speakerIcon = new JLabel(iconVolume);
        volumePanel.add(speakerIcon);
        
        volumeSlider = new JSlider(0, 100);
        volumeSlider.setValue(75);
        volumeSlider.setPreferredSize(new Dimension(100, 20));
        
        volumeSlider.addChangeListener(e -> {
            int sliderValue = volumeSlider.getValue();
            double volume = sliderValue / 100.0;
            playerEngine.setVolume(volume);
        });
        
        volumePanel.add(volumeSlider);
        
        playerControlsPanel.add(volumePanel, BorderLayout.EAST);

        playerControlsPanel.add(buttonPanel, BorderLayout.CENTER);
        playerControlsPanel.add(sliderPanel, BorderLayout.NORTH); 

        add(playerControlsPanel, BorderLayout.SOUTH);
        
        nextButton.addActionListener(e -> {
            playNext();
        });
        
        prevButton.addActionListener(e -> {
            playPrevious();
        });

        playPauseButton.addActionListener(e -> {
            togglePlayPause();
        });
    }

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

    private void togglePlayPause() {
        if (playerEngine.isPlaying()) {
            playerEngine.pause();
            playPauseButton.setIcon(iconPlay);
        } else {
            int selectedRow = songTable.getSelectedRow();

            if (selectedRow == -1) {
                selectedRow = 0;
            }
            
            playSongAtIndex(selectedRow);
        }
    }
    
    private void playSongAtIndex (int index) {
        if (index < 0 || index >= songList.size()) {
            return;
        }
        
        songTable.setRowSelectionInterval(index, index);
        
        Song songToPlay = songList.get(index);
        
        playerEngine.loadSong(songToPlay);
        playerEngine.play();
        
        playPauseButton.setIcon(iconPause);
    }
    
    private void playNext() {
        int currentRow = songTable.getSelectedRow();
        if (currentRow == -1) {
            currentRow = 0;
        }
        
        int nextRow = currentRow + 1;
        if (nextRow >= songList.size()) {
            nextRow = 0;
        }
        
        playSongAtIndex(nextRow);
    }
    
    private void playPrevious() {
        int currentRow = songTable.getSelectedRow();
        if (currentRow == -1) {
            currentRow = 0;
        }
        
        int prevRow = currentRow - 1;
        if (prevRow < 0) {
            prevRow = 0;
        }
        
        playSongAtIndex(prevRow);
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
           }    else if ("songFinished".equals(evt.getPropertyName())) {
               playNext();
           }
       });
    }
    
    public String formatDuration(long durationInSeconds) {
        long minutes = durationInSeconds / 60;
        long seconds = durationInSeconds % 60;
        
        return String.format("%d:%02d", minutes, seconds);
    }
}
