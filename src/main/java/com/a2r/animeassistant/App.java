package com.a2r.animeassistant;

import com.a2r.animeassistant.ui.MainWindow;

// entry point - just launches the window
public class App {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
