package org.one.patientmanagement.ui.core;

import java.awt.Font;
import java.io.InputStream;
import javax.swing.JOptionPane;
import org.one.patientmanagement.PatientManagement;

public class FontLoader {

    public static Font loadFont(String path, float size) {
        try (InputStream is = FontLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Font not found: " + path);
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);

        } catch (Exception e) {
            PatientManagement.showExceptionDialog(e, "Font not found", JOptionPane.WARNING_MESSAGE);
            return new Font("SansSerif", Font.PLAIN, (int) size);
        }
    }
}