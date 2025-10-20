/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.musicplayer;

import java.io.File;
import javax.swing.ImageIcon;
/**
 *
 * @author Raihan
 */
public class Song {
    private String title;
    private String artist;
    private String album;
    private long durationInSecond;
    private String filePath;
    private ImageIcon albumArt;
    private boolean liked = false;

    public Song(String title, String artist, String album, long durationInSecond, String filePath, ImageIcon albumArt) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.durationInSecond = durationInSecond;
        this.filePath = filePath;
        this.albumArt = albumArt;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public long getDurationInSecond() {
        return durationInSecond;
    }

    public String getFilePath() {
        return filePath;
    }

    public ImageIcon getAlbumArt() {
        return albumArt;
    }
    
    public String getFormattedDuration() {
        long minutes = durationInSecond / 60;
        long seconds = durationInSecond % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    public String getFileName() {
        return new File(filePath).getName();
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
    
    

    @Override
    public String toString() {
        return "Song{" +
                "Title='" + title + '\'' +
                ", Artist='" + artist + '\'' +
                ", File Path='" + filePath + '\'' +
                '}';
                
    }
    
    
}
