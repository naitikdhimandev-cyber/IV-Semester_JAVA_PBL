package com.smartexam.ai;

import com.smartexam.db.ViolationDAO;
import org.opencv.core.Mat;

import javax.swing.*;

public class MonitoringService {

    private final int studentId;
    private final int testId;
    private final CameraService cameraService;
    private final FaceVerifier faceVerifier;
    private volatile boolean running = false;
    private Thread monitorThread;

    private int noFaceCount = 0;
    private int multiFaceCount = 0;
    private int attentionCount = 0;
    private static final int VIOLATION_THRESHOLD = 2;

    public MonitoringService(int studentId, int testId) {
        this.studentId = studentId;
        this.testId = testId;
        this.cameraService = new CameraService();
        this.faceVerifier = new FaceVerifier();
    }

    public void start() {
        if (running)
            return;
        running = true;
        monitorThread = new Thread(() -> {
            System.out.println("MonitoringService started for student " + studentId);
            if (!cameraService.startCamera()) {
                System.err.println("MonitoringService: Could not start camera.");
                return;
            }
            try {
                while (running) {
                    Thread.sleep(5500); 
                    Mat frame = cameraService.captureFrame();
                    if (frame == null || frame.empty())
                        continue;

                    int faceCount = faceVerifier.countFaces(frame);
                    double brightness = org.opencv.core.Core.mean(frame).val[0];

                    if (faceCount == 0) {
                        noFaceCount++;
                        multiFaceCount = 0;
                        System.out.println("MonitoringService: NO FACE DETECTED! (Streak: " + noFaceCount
                                + ", Brightness: " + brightness + ")");

                        if (noFaceCount >= VIOLATION_THRESHOLD) {
                            if (brightness < 10) {
                                ViolationDAO.logViolation(studentId, testId, "CAMERA_OBSTRUCTED",
                                        "Camera appears to be covered.");
                                showWarning("Camera error: Lens may be obstructed.");
                            } else {
                                ViolationDAO.logViolation(studentId, testId, "NO_FACE_DETECTED",
                                        "No face seen for 15+ seconds.");
                                showWarning("No face detected! Please stay in view of the camera.");
                            }
                            noFaceCount = 0; // Reset after alert
                        }
                    } else if (faceCount > 1) {
                        multiFaceCount++;
                        noFaceCount = 0;
                        System.out.println(
                                "MonitoringService: MULTIPLE FACES DETECTED! (Streak: " + multiFaceCount + ")");

                        if (multiFaceCount >= VIOLATION_THRESHOLD) {
                            ViolationDAO.logViolation(studentId, testId, "MULTIPLE_FACES",
                                    "Multiple people detected for 15+ seconds.");
                            showWarning("Multiple people detected! Cheating is strictly prohibited.");
                            multiFaceCount = 0; 
                        }
                    } else {
                        noFaceCount = 0;
                        multiFaceCount = 0;
                        org.opencv.core.Rect face = faceVerifier.getMainFace(frame);
                        if (face != null) {
                            double frameCenterX = frame.width() / 2.0;
                            double faceCenterX = face.x + (face.width / 2.0);
                            double faceCenterY = face.y + (face.height / 2.0);

                            // Relaxed boundaries: 40% horizontal offset, 90% height
                            double horizontalOffset = Math.abs(faceCenterX - frameCenterX);
                            double horizontalLimit = frame.width() * 0.40;
                            double verticalLimit = frame.height() * 0.90;

                            System.out.println(String.format(
                                    "Debug: FaceCenter(%.1f, %.1f) OffX(%.1f/%.1f) OffY(%.1f/%.1f)",
                                    faceCenterX, faceCenterY, horizontalOffset, horizontalLimit,
                                    faceCenterY, verticalLimit));

                            boolean lookingAway = horizontalOffset > horizontalLimit || faceCenterY > verticalLimit;

                            if (lookingAway) {
                                attentionCount++;
                                System.out
                                        .println("MonitoringService: ATTENTION LOST! (Streak: " + attentionCount + ")");
                                if (attentionCount >= VIOLATION_THRESHOLD) {
                                    ViolationDAO.logViolation(studentId, testId, "ATTENTION_LOST",
                                            "Student appears to be looking away from screen.");
                                    showWarning("Attention Warning: Please keep your eyes on the screen.");
                                    attentionCount = 0;
                                }
                            } else {
                                attentionCount = 0;
                            }
                        }
                    }
                    // Optional: check for multiple faces, but Haar cascade finds multiple anyway if
                    // we log facesArray.length
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                cameraService.stopCamera();
                System.out.println("MonitoringService stopped.");
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public void stop() {
        running = false;
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
    }

    private void showWarning(String msg) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, msg, "Anti-Cheating Warning", JOptionPane.WARNING_MESSAGE);
        });
    }
}
