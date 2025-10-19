/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.musicplayer;

import com.formdev.flatlaf.FlatDarkLaf;
import javafx.embed.swing.JFXPanel;
import javax.swing.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author Raihan
 */
public class MusicPlayer {

    public static void main(String[] args) {
        
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf()); 
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Gagal mengganti look dan feel");
            e.printStackTrace();
        }
        
        new JFXPanel();
        
        SwingUtilities.invokeLater(() -> {
            MusicFrame mainFrame = new MusicFrame();
            mainFrame.setVisible(true);
        });
    }
}
