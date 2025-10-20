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
import org.jaudiotagger.tag.images.Artwork; // <-- INI IMPORT YANG BENAR
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import javax.imageio.ImageIO;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.CardLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

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
    private JLabel nowPlayingArtLabel;
    private JLabel nowPlayingTitleLabel;
    private JLabel nowPlayingArtistLabel;
    private JPanel mainContentPanel; // <-- Ganti nama dari songListPanel
    private JPanel songsViewPanel;   // <-- Panel untuk tabel lagu (yang lama)
    private JPanel artistsViewPanel; // <-- Panel baru untuk Artis
    private JPanel albumsViewPanel;  // <-- Panel baru untuk Album
    private JPanel playlistsViewPanel; // <-- Panel baru untuk Playlist
    

    private ImageIcon iconPlay;
    private ImageIcon iconPause;
    private ImageIcon iconNext;
    private ImageIcon iconPrev;
    private ImageIcon iconVolume;
    private ImageIcon iconDefaultAlbum;
    private ImageIcon iconSongs;
    private ImageIcon iconArtist;
    private ImageIcon iconAlbumCategory;
    private ImageIcon iconPlaylist;
    private ImageIcon iconProfile;
    private ImageIcon iconHeartEmpty;
    private ImageIcon iconHeartFilled;
    
    
    
    private MusicPlayerEngine playerEngine;
    private List<Song> songList;
    
     private static final String SONGS_VIEW = "SongsView";
     private static final String ARTISTS_VIEW = "ArtistsView";
     private static final String ALBUMS_VIEW = "AlbumsView";
     private static final String PLAYLISTS_VIEW = "PlaylistsView";

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
        
        initTopbarPanel();
        
        initLeftSidebarPanel();
        
        initMainContentPanel();
        initPlayerControlsPanel();
//        loadDummySongs();
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
    
    private ImageIcon loadAndCropToSquareIcon(String path, int targetSize) {
        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResource(path));
            if(originalImage == null) {
                throw new Exception("Gambar tidak ditemukan di path: " + path);
            }
            
            int origW = originalImage.getWidth();
            int origH = originalImage.getHeight();
            
            int cropSize = Math.min(origW, origH);
            int cropX = (origW - cropSize) / 2;
            int cropY = (origH - cropSize) / 2;
            
            BufferedImage croppedImage = originalImage.getSubimage(cropX, cropY, cropSize, cropSize);
            Image scaledImage = croppedImage.getScaledInstance(targetSize, targetSize, Image.SCALE_SMOOTH);
            
            return new ImageIcon(scaledImage);
        }   catch(Exception e) {
            System.err.println("Gagal load dan crop ikon: " + path);
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
        iconSongs = loadAndScaleIcon("/icons/songs.png", iconSize, iconSize);
        iconArtist = loadAndScaleIcon("/icons/artist category.png", iconSize, iconSize);
        iconAlbumCategory = loadAndScaleIcon("/icons/album.png", iconSize, iconSize);
        iconPlaylist = loadAndScaleIcon("/icons/playlist.png", iconSize, iconSize);
        iconHeartEmpty = loadAndScaleIcon("/icons/heart_empty.png", 20, 20);
        iconHeartFilled = loadAndScaleIcon("/icons/heart_filled.png", 20, 20);
        
        iconProfile = loadAndCropToSquareIcon("/icons/profile.png", 150);
        iconDefaultAlbum = loadAndScaleIcon("/icons/artist.png", 60, 60);
    }
    
    private void initTopbarPanel() {
        topBarPanel = new JPanel(new BorderLayout(10, 0));
        topBarPanel.setPreferredSize(new Dimension(getWidth(), 50));
        topBarPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        ImageIcon logoIcon = loadAndScaleIcon("/icons/icon.png", 32, 32);
        JLabel logoLabel = new JLabel(logoIcon);
        leftPanel.add(logoLabel);
        JLabel userNameLabel = new JLabel("Music Player Kelompok 4");
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        leftPanel.add(userNameLabel);
        topBarPanel.add(leftPanel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton addFilesButton = new JButton("Add Files");
        
        addFilesButton.addActionListener(e -> {
            addSongsFromFileChooser();
        });
        
        rightPanel.add(addFilesButton);
        
        topBarPanel.add(rightPanel, BorderLayout.EAST);
        
        add(topBarPanel, BorderLayout.NORTH);
    }
    
    private void initLeftSidebarPanel() {
        leftSidebarPanel = new JPanel(new BorderLayout()); 
        leftSidebarPanel.setPreferredSize(new Dimension(180, getHeight()));

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 5, 5));
        
        JLabel profileIconLabel = new JLabel(iconProfile);
        profileIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel userNameLabel = new JLabel("Raihan");
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        profilePanel.add(profileIconLabel);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        profilePanel.add(userNameLabel);

        leftSidebarPanel.add(profilePanel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 

        String[] menuItems = {"Songs", "Artists", "Albums", "Playlists"}; 
        String[] iconPaths = {"/icons/songs.png", "/icons/artist.png", "/icons/album.png", "/icons/playlist.png"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = new JButton(menuItems[i]);
            
            menuButton.setBorderPainted(false);
            menuButton.setContentAreaFilled(false);
            menuButton.setFocusPainted(false);
            menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            menuButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            menuButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            ImageIcon icon = loadAndScaleIcon(iconPaths[i], 24, 24); 
            if (icon != null) { 
                menuButton.setIcon(icon);
            }
            menuButton.setIconTextGap(15);

            menuPanel.add(menuButton);

            if (i < menuItems.length - 1) {
                 menuPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
            }

            final String menuItemName = menuItems[i]; 
            menuButton.addActionListener(e -> {
                CardLayout cl = (CardLayout) (mainContentPanel.getLayout());
                
                String panelToShow = SONGS_VIEW;
                if (menuItemName.equals("Artists")) {
                    panelToShow = ARTISTS_VIEW;
                } else if (menuItemName.equals("Albums")) {
                    panelToShow = ALBUMS_VIEW;
                } else if (menuItemName.equals("Playlists")) {
                    panelToShow = PLAYLISTS_VIEW;
                } 
                
                cl.show(mainContentPanel, panelToShow);
            });
        } 

        leftSidebarPanel.add(menuPanel, BorderLayout.CENTER);

        add(leftSidebarPanel, BorderLayout.WEST);
    }
    
    private Song createSongFromMetadata(String filePath) {
        try {
            File file = new File(filePath);
            AudioFile audioFile = AudioFileIO.read(file);
            
            Tag tag = audioFile.getTag();
            
            long duration = audioFile.getAudioHeader().getTrackLength();
            
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
            
            Artwork artwork = tag.getFirstArtwork(); 
            ImageIcon albumArtIcon = iconDefaultAlbum; 
            
            if (artwork != null) {
                try {
                    byte[] imageData = artwork.getBinaryData();
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
                    
                    Image scaledImg = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                    albumArtIcon = new ImageIcon(scaledImg);
                } catch (Exception e) {
                    System.err.println("Gagal load album art untuk: " + title);
                }
            }

            System.out.println(">>> jaudiotagger BERHASIL baca: " + title);
            
            return new Song(title, artist, album, duration, filePath, albumArtIcon);
            
        }   catch (Exception e) {
            System.err.println("Error membaca metadata dari: " + filePath);
            e.printStackTrace();
            return null;
        }
    }
    
    private void initMainContentPanel() {
        // 1. Buat panel induk dengan CardLayout
        mainContentPanel = new JPanel(new CardLayout());

        // 2. Buat Panel untuk View "Songs" (Isinya kode tabel lama)
        songsViewPanel = new JPanel(new BorderLayout()); // Panel ini pakai BorderLayout
        String[] columnNames = {"Judul", "Artis", "Durasi"};
        tableModel = new DefaultTableModel(columnNames, 0) { /* ... isCellEditable ... */ };
        songTable = new JTable(tableModel);
        // ... (Semua kode styling JTable: setRowHeight, setFont, dll.) ...
        songTable.setRowHeight(30);
        songTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        songTable.setShowGrid(false);
        songTable.setIntercellSpacing(new Dimension(0, 0));
        songTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(songTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        songsViewPanel.add(scrollPane, BorderLayout.CENTER);
        // Tambahkan songsViewPanel ke CardLayout dengan NAMA
        mainContentPanel.add(songsViewPanel, SONGS_VIEW);

        // 3. Buat Panel Placeholder untuk View "Artists"
        artistsViewPanel = new JPanel();
        artistsViewPanel.add(new JLabel("Tampilan Daftar Artis (Belum Dibuat)")); // Placeholder
        // Tambahkan artistsViewPanel ke CardLayout dengan NAMA
        mainContentPanel.add(artistsViewPanel, ARTISTS_VIEW);

        // 4. Buat Panel Placeholder untuk View "Albums"
        albumsViewPanel = new JPanel();
        albumsViewPanel.add(new JLabel("Tampilan Daftar Album (Belum Dibuat)")); // Placeholder
        // Tambahkan albumsViewPanel ke CardLayout dengan NAMA
        mainContentPanel.add(albumsViewPanel, ALBUMS_VIEW);

        // 5. Buat Panel Placeholder untuk View "Playlists"
        playlistsViewPanel = new JPanel();
        playlistsViewPanel.add(new JLabel("Tampilan Daftar Playlist (Belum Dibuat)")); // Placeholder
        // Tambahkan playlistsViewPanel ke CardLayout dengan NAMA
        mainContentPanel.add(playlistsViewPanel, PLAYLISTS_VIEW);


        // 6. Tambahkan panel induk (mainContentPanel) ke frame
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void initPlayerControlsPanel() {
        playerControlsPanel = new JPanel(new BorderLayout()); 
        playerControlsPanel.setPreferredSize(new Dimension(getWidth(), 90));

        JPanel nowPlayingPanel = new JPanel(new BorderLayout(10, 0));
        nowPlayingPanel.setPreferredSize(new Dimension(250, 90));
        nowPlayingPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        nowPlayingArtLabel = new JLabel(iconDefaultAlbum);
        nowPlayingArtLabel.setPreferredSize(new Dimension (64, 64));
        nowPlayingPanel.add(nowPlayingArtLabel, BorderLayout.WEST);
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        nowPlayingTitleLabel = new JLabel("Pilih Lagu");
        nowPlayingTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nowPlayingArtistLabel = new JLabel(" ");
        nowPlayingArtistLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textPanel.add(nowPlayingTitleLabel);
        textPanel.add(nowPlayingArtistLabel);
        nowPlayingPanel.add(textPanel, BorderLayout.CENTER);

        playerControlsPanel.add(nowPlayingPanel, BorderLayout.WEST);

        JPanel volumePanel = new JPanel(new FlowLayout()); 
        volumePanel.setPreferredSize(new Dimension(150, 90)); 
        JLabel speakerIcon = new JLabel(iconVolume); 
        volumePanel.add(speakerIcon);
        volumeSlider = new JSlider(0, 100); 
        volumeSlider.setValue(75); 
        volumeSlider.setPreferredSize(new Dimension(100, 20)); 
        volumeSlider.addChangeListener(e -> {
            System.out.println(">>> UI: volumeSlider Value Changed!"); 

            int sliderValue = volumeSlider.getValue();
            double volume = sliderValue / 100.0;
            playerEngine.setVolume(volume);
        });
        volumePanel.add(volumeSlider);

        playerControlsPanel.add(volumePanel, BorderLayout.EAST);

        // === Panel Tengah Baru (CENTER) ===
        JPanel centerPanel = new JPanel(new BorderLayout()); 

        JPanel sliderPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        sliderPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0)); 

        songSlider = new JSlider();
        songSlider.setValue(0);
        songSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println(">>> UI: songSlider Mouse Released!"); 
                
                int newSeconds = songSlider.getValue();
                playerEngine.seek(newSeconds);
                timeStartLabel.setText(formatDuration(newSeconds));
            }
        });
        
        timeStartLabel = new JLabel("0:00");
        timeEndLabel = new JLabel("0:00");
        
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 10, 0, 5);
        gbc.anchor = GridBagConstraints.LINE_END; 
        sliderPanel.add(timeStartLabel, gbc);

        gbc.gridx = 1; 
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER; 
        sliderPanel.add(songSlider, gbc);

        gbc.gridx = 2; 
        gbc.weightx = 0; 
        gbc.fill = GridBagConstraints.NONE; 
        gbc.insets = new Insets(0, 5, 0, 10); 
        gbc.anchor = GridBagConstraints.LINE_START; 
        sliderPanel.add(timeEndLabel, gbc);

        centerPanel.add(sliderPanel, BorderLayout.NORTH);


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
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        playerControlsPanel.add(centerPanel, BorderLayout.CENTER);


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
    
    private void addSongsFromFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 Files", "mp3");
        fileChooser.setFileFilter(filter);
        
        fileChooser.setMultiSelectionEnabled(true);
        
        int result = fileChooser.showOpenDialog(this);
        
        
        if(result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFile = fileChooser.getSelectedFiles();
            
            for(File file : selectedFile) {
                String filePath = file.getAbsolutePath();
                
                boolean alreadyExists = false;
                for (Song existingSong : songList) {
                    if (existingSong.getFilePath().equals(filePath)) {
                        alreadyExists = true;
                        break;
                    }
                }
                
                if (!alreadyExists) {
                    Song newSong = createSongFromMetadata(filePath);
                    if(newSong != null) {
                        songList.add(newSong);
                        
                        Object[] rowData = {
                            newSong.getTitle(),
                            newSong.getArtist(),
                            newSong.getFormattedDuration()
                        };
                        tableModel.addRow(rowData);
                    }
                }   else {
                    System.out.println("Lagu Sudah Ada: " + file.getName());
                }
            }

        }
    }

    private void playSongAtIndex(int index) {
        if (index < 0 || index >= songList.size()) {
            return; 
        }
        songTable.setRowSelectionInterval(index, index);
        Song songToPlay = songList.get(index);
        playerEngine.loadSong(songToPlay);
        playerEngine.play();
        playPauseButton.setIcon(iconPause);
        
        nowPlayingArtLabel.setIcon(songToPlay.getAlbumArt());
        nowPlayingTitleLabel.setText(songToPlay.getTitle());
        nowPlayingArtistLabel.setText(songToPlay.getArtist());
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
            prevRow = songList.size() - 1;
        }
        
        playSongAtIndex(prevRow);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
       SwingUtilities.invokeLater(() ->{
           
           if ("songReady".equals(evt.getPropertyName())) {
               double totalSeconds = (double) evt.getNewValue();
               songSlider.setMaximum((int) totalSeconds);
               timeEndLabel.setText(formatDuration((long) totalSeconds));
               
           } else if ("timeUpdate".equals(evt.getPropertyName())) {
               double[] times = (double[]) evt.getNewValue();
               double currentSeconds = times[0];
               if(!songSlider.getValueIsAdjusting()) {
                   songSlider.setValue((int) currentSeconds);
               }
               timeStartLabel.setText(formatDuration((long) currentSeconds));
               
           } else if ("songFinished".equals(evt.getPropertyName())) {
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