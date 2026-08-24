package com.smartexam.ai;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FaceVerifier {

    private final CascadeClassifier faceDetector;

    public FaceVerifier() {
        String path = resolveCascadePath();
        faceDetector = new CascadeClassifier(path);
        if (faceDetector.empty()) {
            System.err.println("FaceVerifier: Haar cascade did not load. Path tried: " + path);
        }
    }

    /**
     * Resolves {@code resources/haarcascade_frontalface_default.xml} from the
     * process working directory
     * (run the app from the project folder) so face detection works even when CWD
     * is not obvious.
     */
    private static String resolveCascadePath() {
        Path rel = Paths.get("resources", "haarcascade_frontalface_default.xml");
        if (Files.exists(rel)) {
            return rel.toAbsolutePath().normalize().toString();
        }
        Path fromUserDir = Paths.get(System.getProperty("user.dir", "."), "resources",
                "haarcascade_frontalface_default.xml");
        if (Files.exists(fromUserDir)) {
            return fromUserDir.toAbsolutePath().normalize().toString();
        }
        return rel.toString();
    }

    public boolean cascadeLoaded() {
        return faceDetector != null && !faceDetector.empty();
    }

    public boolean isFacePresent(Mat frame) {
        if (faceDetector == null || faceDetector.empty()) {
            return false;
        }
        if (frame == null || frame.empty()) {
            return false;
        }
        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(gray, gray);

        MatOfRect faces = new MatOfRect();
        // Standard parameters: 1.1 scale, 3 minNeighbors
        faceDetector.detectMultiScale(gray, faces, 1.1, 3, 0, new Size(30, 30), new Size());

        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 1) {
            System.out.println("Debug: MULTIPLE FACES DETECTED: " + facesArray.length);
        } else if (facesArray.length == 0) {
            System.out.println("Debug: NO FACE DETECTED.");
        } else {
            System.out.println("Debug: Exactly one face detected.");
        }

        return facesArray.length > 0;
    }

    public int countFaces(Mat frame) {
        if (faceDetector == null || faceDetector.empty() || frame == null || frame.empty()) {
            return 0;
        }
        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(gray, gray);
        MatOfRect faces = new MatOfRect();

        // Increased minNeighbors to 5 to reduce noise/false positives
        // Using a larger minSize (60x60) to ignore small background artifacts
        faceDetector.detectMultiScale(gray, faces, 1.1, 5, 0, new Size(60, 60), new Size());
        return faces.toArray().length;
    }

    public org.opencv.core.Rect getMainFace(Mat frame) {
        if (faceDetector == null || faceDetector.empty() || frame == null || frame.empty()) {
            return null;
        }
        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(gray, gray);
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(gray, faces, 1.1, 5, 0, new Size(60, 60), new Size());

        Rect[] facesArray = faces.toArray();
        if (facesArray.length == 0)
            return null;

        // Return the largest face found (most likely the student)
        Rect largest = facesArray[0];
        for (Rect r : facesArray) {
            if (r.area() > largest.area())
                largest = r;
        }
        return largest;
    }
}
