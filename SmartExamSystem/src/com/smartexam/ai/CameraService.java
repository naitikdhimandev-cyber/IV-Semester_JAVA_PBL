package com.smartexam.ai;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Core;

public class CameraService {

    private static final boolean NATIVE_LOAD_OK;

    static {
        boolean ok = false;
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            ok = true;
        } catch (UnsatisfiedLinkError e) {
            // errors  ---------------------------------------------------------- +1   ++++++++++++
            System.err.println("OpenCV native library not loaded: " + Core.NATIVE_LIBRARY_NAME);
            System.err.println("Add the folder containing libopencv_java*.dylib (macOS) to -Djava.library.path");
            e.printStackTrace();
        }
        NATIVE_LOAD_OK = ok;
    }

    public static boolean isOpenCvNativeLoaded() {
        return NATIVE_LOAD_OK;
    }

    private VideoCapture camera;

    public boolean startCamera() {
        camera = new VideoCapture(0);
        return camera.isOpened();
    }

    public void stopCamera() {
        if (camera != null && camera.isOpened()) {
            camera.release();
        }
    }

    public Mat captureFrame() {
        if (camera == null || !camera.isOpened()) {
            return null;
        }
        Mat frame = new Mat();
        if (camera.read(frame) && !frame.empty()) {
            return frame;
        }
        return null;
    }
}
