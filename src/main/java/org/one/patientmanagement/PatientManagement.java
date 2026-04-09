package org.one.patientmanagement;

import com.google.inject.Guice;
import java.awt.EventQueue;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.one.patientmanagement.repository.RepositoryModule;
import org.one.patientmanagement.service.ServiceModule;
import org.one.patientmanagement.storage.DatabaseInitializer;
import org.one.patientmanagement.storage.DatabaseModule;
import org.one.patientmanagement.ui.MainView;
import org.one.patientmanagement.ui.PresentationModule;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Font;
import javax.swing.UIManager;
import org.one.patientmanagement.ui.core.Theme;

public class PatientManagement {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                System.out.println("Hello World!");
                
                Theme.setup();
                FlatMacLightLaf.registerCustomDefaultsSource("themes");
                FlatMacLightLaf.setup();
                
                var injector = Guice.createInjector(
                        new DatabaseModule(),
                        new RepositoryModule(),
                        new ServiceModule()
//                        new PresentationModule()
                );

                // TODO: exception handling for the storage
                injector.getInstance(DatabaseInitializer.class).init();
                
                if (args.length > 0)
                    switch(args[0]) {
                        case "doctor" -> new DoctorAppContext(injector).start();
                        case "patient", "default" -> new PatientAppContext(injector).start();
                    }
                else new PatientAppContext(injector).start();
            } catch (Exception e) {
                e.printStackTrace();
                showExceptionDialog(e, "Failed to start", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void showExceptionDialog(Exception e, String title, int type) {
        JLabel label = new JLabel("Error details:");
        label.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        JTextArea textArea = new JTextArea(e.getMessage());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 200));
        scrollPane.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(scrollPane);

        JOptionPane.showMessageDialog(
                null,
                panel,
                title,
                type
        );
    }
}
