package io.fotoapparat.routine;

import io.fotoapparat.hardware.CameraDevice;
import io.fotoapparat.preview.FramePreProcessor;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.preview.PreviewStream;

/**
 * Configures {@link PreviewStream} of the camera.
 */
public class ConfigurePreviewStreamRoutine implements Runnable {

    private final CameraDevice cameraDevice;
    private final FramePreProcessor framePreProcessor;
    private final FrameProcessor frameProcessor;

    public ConfigurePreviewStreamRoutine(CameraDevice cameraDevice,
                                         FramePreProcessor framePreProcessor,
                                         FrameProcessor frameProcessor) {
        this.cameraDevice = cameraDevice;
        this.framePreProcessor = framePreProcessor;
        this.frameProcessor = frameProcessor;
    }

    @Override
    public void run() {
        if (frameProcessor == null) {
            return;
        }

        PreviewStream previewStream = cameraDevice.getPreviewStream();

        previewStream.setPreprocessor(framePreProcessor);
        previewStream.addProcessor(frameProcessor);
        previewStream.start();
    }

}
