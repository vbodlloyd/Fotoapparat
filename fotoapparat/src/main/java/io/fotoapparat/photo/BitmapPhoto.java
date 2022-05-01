package io.fotoapparat.photo;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import io.fotoapparat.lens.CaptureMetadata;

/**
 * Photo as {@link Bitmap}.
 */
public class BitmapPhoto {

    /**
     * {@link Bitmap} of the photo.
     */
    public final Bitmap bitmap;

    /**
     * Clockwise rotation relatively to screen orientation at the moment when photo was taken.
     */
    public final int rotationDegrees;

    @Nullable
    private final CaptureMetadata metadata;

    public BitmapPhoto(Bitmap bitmap,
                       int rotationDegrees,
                       @Nullable final CaptureMetadata metadata) {
        this.bitmap = bitmap;
        this.rotationDegrees = rotationDegrees;
        this.metadata = metadata;
    }

    @Nullable
    public CaptureMetadata getMetadata() {
        return metadata;
    }
}
