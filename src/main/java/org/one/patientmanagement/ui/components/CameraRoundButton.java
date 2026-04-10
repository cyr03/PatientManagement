/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.one.patientmanagement.ui.components;

import javax.swing.*;
import java.awt.*;

public class CameraRoundButton extends JButton {

    public CameraRoundButton() {
        setPreferredSize(new Dimension(80, 80));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // outer circle (border)
        g2.setColor(new Color(220, 220, 220));
        g2.fillOval(0, 0, getWidth(), getHeight());

        // inner circle (main button)
        int margin = 8;
        if (getModel().isPressed()) {
            g2.setColor(new Color(180, 180, 180));
        } else {
            g2.setColor(Color.WHITE);
        }

        g2.fillOval(margin, margin,
                getWidth() - margin * 2,
                getHeight() - margin * 2);

        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(180, 180, 180));
        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

        g2.dispose();
    }
}