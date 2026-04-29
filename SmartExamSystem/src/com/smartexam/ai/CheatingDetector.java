package com.smartexam.ai;

import org.opencv.core.Mat;

import javax.swing.*;

public class CheatingDetector {

    public static boolean checkBeforeExam() {
        try {
            if (!CameraService.isOpenCvNativeLoaded()) {
                JOptionPane.showMessageDialog(null,
                        "OpenCV native library failed to load.\n\n"
                                + "Face detection is REQUIRED to start the exam.\n"
                                + "Please ensure OpenCV is correctly installed.");
                return false;
            }
            CameraService cameraService = new CameraService();
            if (!cameraService.startCamera()) {
                JOptionPane.showMessageDialog(null, "Could not access camera. Face check is REQUIRED.");
                return false;
            }

            Mat frame = new Mat();
            for (int i = 0; i < 20; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                frame = cameraService.captureFrame();
                if (frame != null && !frame.empty())
                    break;
            }

            cameraService.stopCamera();

            FaceVerifier verifier = new FaceVerifier();
            if (!verifier.cascadeLoaded()) {
                JOptionPane.showMessageDialog(null,
                        "Face cascade file could not be loaded.\n"
                                + "Face detection is REQUIRED to start the exam.");
                return false;
            }
            boolean hasFace = verifier.isFacePresent(frame);
            if (!hasFace) {
                JOptionPane.showMessageDialog(null, "No face detected. Exam blocked.");
                return false;
            }
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(null, "Face detection error: " + t.getMessage() + "\nExam cannot start.");
            return false;
        }
    }
}
