package org.one.patientmanagement.ui.core;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

public class Theme {
    private Theme() {}
    
    public static void setup() {
        setupTypography();
    }
    
    private static void setupTypography() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        ge.registerFont(createFont("Manrope-Medium.ttf"));
        ge.registerFont(createFont("Manrope-Regular.ttf"));
        ge.registerFont(createFont("Manrope-SemiBold.ttf"));
        ge.registerFont(createFont("Poppins-Medium.ttf"));
        ge.registerFont(createFont("Poppins-Regular.ttf"));
        ge.registerFont(createFont("Poppins-SemiBold.ttf"));
    }
    
    private static Font createFont(String font) {
        try {
            var path = "/fonts/";
            return Font.createFont(
                Font.TRUETYPE_FONT,
                Theme.class.getResourceAsStream(path + font)
            );
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException("Failed to load font: " + font, e);
        }
    }
}
