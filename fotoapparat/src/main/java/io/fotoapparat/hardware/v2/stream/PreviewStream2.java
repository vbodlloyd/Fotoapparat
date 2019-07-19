package io.fotoapparat.hardware.v2.stream;

import android.media.Image;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

import io.fotoapparat.hardware.v2.orientation.OrientationManager;
import io.fotoapparat.hardware.v2.parameters.ParametersProvider;
import io.fotoapparat.log.Logger;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FramePreProcessor;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.preview.PreviewStream;
import io.fotoapparat.util.YUVUtil;

/**
 * {@link PreviewStream} of Camera v2.
 */
@SuppressWarnings("NewApi")
public class PreviewStream2 implements PreviewStream,
        OnImageAcquiredObserver.OnFrameAcquiredListener {

    private final OnImageAcquiredObserver imageAcquiredObserver;
    private final ParametersProvider parametersProvider;
    private final OrientationManager orientationManager;
    private final Logger logger;

    @Nullable
    private FramePreProcessor framePreProcessor = null;
    private final Set<FrameProcessor> frameProcessors = new LinkedHashSet<>();

    public PreviewStream2(OnImageAcquiredObserver imageAcquiredObserver,
                          ParametersProvider parametersProvider,
                          OrientationManager orientationManager,
                          Logger logger) {
        this.imageAcquiredObserver = imageAcquiredObserver;
        this.parametersProvider = parametersProvider;
        this.orientationManager = orientationManager;
        this.logger = logger;
    }

    @Override
    public void addFrameToBuffer() {
        // Does nothing
    }

    @Override
    public void setPreprocessor(@Nullable FramePreProcessor preProcessor) {
        framePreProcessor = preProcessor;
    }

    @Override
    public void addProcessor(@NonNull FrameProcessor processor) {
        synchronized (frameProcessors) {
            frameProcessors.add(processor);
        }
    }

    @Override
    public void removeProcessor(@NonNull FrameProcessor processor) {
        synchronized (frameProcessors) {
            frameProcessors.remove(processor);
        }
    }

    @Override
    public void start() {
        imageAcquiredObserver.setListener(this);
    }

    @Override
    public byte[] onPreProcessFrame(Image frame) {
        final FramePreProcessor preProcessor = framePreProcessor;
        if (preProcessor != null) {
            return preProcessor.preProcessFrame(frame);

        } else {
            return YUVUtil.imageToByte(frame);
        }
    }

    @Override
    public void onFrameAcquired(byte[] bytes) {
        synchronized (frameProcessors) {
            dispatchFrame(bytes);
        }
    }

    private void dispatchFrame(byte[] image) {
        final Frame frame = new Frame(
                parametersProvider.getPreviewSize(),
                image,
                orientationManager.getPhotoOrientation()
        );

        for (FrameProcessor frameProcessor : frameProcessors) {
            frameProcessor.processFrame(frame);
        }
    }
}
