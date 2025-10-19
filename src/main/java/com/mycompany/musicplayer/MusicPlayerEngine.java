/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.musicplayer;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import javafx.util.Duration;
/**
 *
 * @author Raihanz
 */
public class MusicPlayerEngine {
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private boolean isPlaying = false;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    
    public void loadSong(Song song) {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        
        try {
            this.currentSong = song;
            File file = new File(song.getFilePath());
            String uri = file.toURI().toString();
            
            Media media = new Media(uri);
            
            mediaPlayer = new MediaPlayer(media);
            
            System.out.println(">>> javaFX berhasil di load: " + song.getTitle());
            
            mediaPlayer.setOnEndOfMedia(() -> {
                isPlaying = false;
                System.out.println("Lagu Selesai: " + currentSong.getTitle());
                mediaPlayer.getError().printStackTrace();
            });
            
            mediaPlayer.setOnReady(() -> {
               Duration totalDuration = mediaPlayer.getTotalDuration();
               support.firePropertyChange("songReady", null, totalDuration.toSeconds());
            });
            
            mediaPlayer.currentTimeProperty().addListener((observable, oldTime, newTime) -> {
                Duration totalDuration = mediaPlayer.getTotalDuration();
                
                double[] times = { newTime.toSeconds(), totalDuration.toSeconds() };
                support.firePropertyChange("timeUpdate", null, times);
            });
            
            isPlaying = false;
            
        } catch (Exception e) {
            System.err.println("!!! javaFx gagal load: " + song.getTitle());
            e.printStackTrace();
            mediaPlayer = null;
        }
    }
    
    public void play () {
        if (mediaPlayer != null) {
            System.out.println("Mesin Play: Berhasil memutar lagu");
            mediaPlayer.play();
            isPlaying = true;
        }   else {
            System.err.println("Mesil Play: Gagal, mediaPlayer = null");
        }
    }
    
    public void pause () {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }
    
    public void stop () {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
        }
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public Song getCurrentSong() {
        return currentSong;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
    
    public void seek(int seconds) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(seconds));
        }
    }
}
