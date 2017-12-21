package io.fotoapparat.routine;

import io.fotoapparat.error.CameraErrorCallback;
import io.fotoapparat.hardware.CameraDevice;
import io.fotoapparat.hardware.CameraException;

/**
 * Stops preview and closes the camera.
 */
public class StopCameraRoutine implements Runnable {

    private final CameraDevice cameraDevice;
    private final CameraErrorCallback cameraErrorCallback;

    public StopCameraRoutine(CameraDevice cameraDevice, CameraErrorCallback cameraErrorCallback) {
        this.cameraDevice = cameraDevice;
        this.cameraErrorCallback = cameraErrorCallback;
    }

    @Override
    public void run() {
        try {
            cameraDevice.stopPreview();
            cameraDevice.close();
        } catch (CameraException e) {
            cameraErrorCallback.onError(e);
        }
    }

}
