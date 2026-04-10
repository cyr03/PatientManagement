package org.one.patientmanagement.ui.controller.navigation.doctor;

import java.awt.CardLayout;
import javax.swing.JPanel;
import org.one.patientmanagement.ui.controller.navigation.AbstractNavigator;

public class DoctorNavigator extends AbstractNavigator<DoctorRoute> {

    public DoctorNavigator(CardLayout layout, JPanel container) {
        super(layout, container);
    }
    
}
