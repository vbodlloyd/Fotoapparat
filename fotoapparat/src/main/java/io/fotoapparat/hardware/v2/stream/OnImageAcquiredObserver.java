package io.fotoapparat.hardware.v2.stream;

import android.media.Image;

/**
 * Observer which accepts a {@link OnFrameAcquiredListener}.
 */
public interface OnImageAcquiredObserver {

    /**
     * Sets a {@link OnFrameAcquiredListener}.
     *
     * @param listener The listener to be used.
     */
    void setListener(OnFrameAcquiredListener listener);

    /**
     * Notified when an image has been acquired.
     */
    interface OnFrameAcquiredListener {

        /**
         * Called when an image has been acquired and need to be converted into a byte array
         * @param frame The acquired {@link Image}
         * @return The image as a byte array.
         */
        byte[] onPreProcessFrame(Image frame);

        /**
         * Called when an image has been acquired.
         *
         * @param bytes The image as a byte array.
         */
        void onFrameAcquired(byte[] bytes);

    }
}

