package org.one.patientmanagement.ui.view;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.one.patientmanagement.PatientManagement;

public class CameraView extends JPanel {

    private Webcam webcam;
    private WebcamPanel webcamPanel;
    // 1. Fix doLayout to handle ANY child component, not just webcamPanel
    private final JPanel squareContainer = new JPanel(null) { // null layout = manual
        @Override
        public void doLayout() {
            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            // Apply bounds to ALL children, not just webcamPanel
            for (java.awt.Component c : getComponents()) {
                c.setBounds(x, y, size, size);
            }
        }
    };

    public CameraView() {
        setLayout(new BorderLayout());
        webcam = Webcam.getWebcams().get(0);

        squareContainer.setLayout(new BorderLayout());
        addWebcamPanel();
        add(squareContainer, BorderLayout.CENTER);
    }

    // 3. Also fix addWebcamPanel() to match — remove BorderLayout constraint
//    since squareContainer now uses null layout
    private void addWebcamPanel() {
        squareContainer.removeAll();
        webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setMirrored(true);
        squareContainer.add(webcamPanel); // no BorderLayout.CENTER
        squareContainer.revalidate();
        squareContainer.repaint();
    }

    // 2. Stop webcamPanel before removing it in addResult()
    private void addResult(BufferedImage image) {
        // ⭐ Stop the webcam stream first
        if (webcamPanel != null) {
            webcamPanel.stop();
        }

        squareContainer.removeAll();

        JPanel imagePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image == null) {
                    return;
                }
                int w = getWidth();
                int h = getHeight();
                if (w == 0 || h == 0) {
                    return;
                }
                int iw = image.getWidth();
                int ih = image.getHeight();
                double scale = Math.min((double) w / iw, (double) h / ih);
                int nw = (int) (iw * scale);
                int nh = (int) (ih * scale);
                int x = (w - nw) / 2;
                int y = (h - nh) / 2;
                g.drawImage(image, x, y, nw, nh, this);
            }
        };

        squareContainer.add(imagePanel);
        squareContainer.revalidate();
        squareContainer.repaint();

        // ⭐ Repaint AFTER layout has run and imagePanel has a real size
        javax.swing.SwingUtilities.invokeLater(() -> {
            imagePanel.revalidate();
            imagePanel.repaint();
        });
    }

    public Webcam getWebcam() {
        return webcam;
    }

    public void restart(Webcam webcam) {
        webcamPanel.stop();
        this.webcam.close();
        this.webcam = webcam;
        webcam.open();

        addWebcamPanel();

        revalidate();
        repaint();
    }

    public void takePhoto() {
        try {
            BufferedImage image = webcam.getImage();

            addResult(image);

        } catch (Exception ex) {
            ex.printStackTrace();
            PatientManagement.showExceptionDialog(ex, "Take photo error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
