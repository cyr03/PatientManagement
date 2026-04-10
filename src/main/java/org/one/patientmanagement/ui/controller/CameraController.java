/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.one.patientmanagement.ui.controller;

import com.github.sarxos.webcam.Webcam;
import javax.swing.JComboBox;
import org.one.patientmanagement.ui.components.CameraControl;
import org.one.patientmanagement.ui.view.CameraView;

/**
 *
 * @author KAROL JOHN
 */
public class CameraController {

    private final CameraControl cameraControl;
    private final CameraView cameraView;

    public CameraController(CameraControl cameraControl, CameraView cameraView) {
        this.cameraControl = cameraControl;
        this.cameraView = cameraView;
        
        var webcams = Webcam.getWebcams();
        
        cameraControl.populateWebcams(webcams);
        
        final JComboBox camerasComboBox = cameraControl.getCamerasComboBox();

        cameraControl.getShutter().addActionListener(e -> {
            cameraView.takePhoto();
        });
        camerasComboBox.addActionListener(e -> {
            Webcam webcam = cameraView.getWebcam();
            
            int index = camerasComboBox.getSelectedIndex();
            if (index < 0 || index >= webcams.size()) {
                return;
            }

            if (webcam != null && webcam.isOpen()) {
                webcam.close();
            }
            
            cameraView.restart(webcams.get(index));
        });
    }

}
