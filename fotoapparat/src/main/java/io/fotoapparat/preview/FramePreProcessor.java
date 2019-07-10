package io.fotoapparat.preview;

import android.media.Image;

import android.support.annotation.NonNull;

import io.fotoapparat.parameter.Size;

/**
 * Allows to convert frames before forwarding it to the {@link FrameProcessor}.
 */
public interface FramePreProcessor {

    /**
     * Performs preprocessing on preview frames. Called from the camera thread
     *
     * @param frame {@link Image} of the preview. Do not cache it as it will eventually be reused by the
     *              camera.
     * @return The image as a byte array.
     */
    byte[] preProcessFrame(@NonNull Image frame);

    /**
     * Performs preprocessing on preview frames. Called from a background thread
     *
     * @param frame byte array containing the frame of the preview. Do not cache it as it will eventually be reused by the
     *              camera.
     * @return byte array of the converted image to send to the {@link FrameProcessor}.
     */
    byte[] preProcessFrame(@NonNull byte[] frame, @NonNull Size previewSize);
}
