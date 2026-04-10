package org.one.patientmanagement;

import com.google.inject.Injector;
import org.one.patientmanagement.ui.DoctorFrame;

public class DoctorAppContext {

    private final Injector injector;
    
    public DoctorAppContext(Injector injector) {
        this.injector = injector;
    }
    
    public void start() {
        injector.getInstance(DoctorFrame.class).setVisible(true);
    }
}
