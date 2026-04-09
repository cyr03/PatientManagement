package org.one.patientmanagement;

import com.google.inject.Injector;
import org.one.patientmanagement.ui.PatientFrame;

public class PatientAppContext {

    private final Injector injector;
    
    public PatientAppContext(Injector injector) {
        this.injector = injector;
    }
    
    public void start() {
        injector.getInstance(PatientFrame.class).setVisible(true);
    }
}
