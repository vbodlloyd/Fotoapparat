package io.fotoapparat.photo;

import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Objects;

import io.fotoapparat.lens.CaptureMetadata;

/**
 * Taken photo.
 */
public class Photo {

    /**
     * Encoded image. Use {@link android.graphics.BitmapFactory#decodeByteArray(byte[], int, int)}
     * to decode it.
     */
    public final byte[] encodedImage;

    /**
     * Clockwise rotation relatively to screen orientation at the moment when photo was taken. To
     * display the photo in a correct orientation it needs to be rotated counter clockwise by this
     * value.
     */
    public final int rotationDegrees;

    @Nullable
    private final CaptureMetadata metadata;

    public Photo(byte[] encodedImage,
                 int rotationDegrees,
                 @Nullable final CaptureMetadata metadata) {
        this.encodedImage = encodedImage;
        this.rotationDegrees = rotationDegrees;
        this.metadata = metadata;
    }

    /**
     * @return empty {@link Photo}.
     */
    public static Photo empty() {
        return new Photo(new byte[0], 0, null);
    }

    @Nullable
    public CaptureMetadata getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        return rotationDegrees == photo.rotationDegrees
                && Arrays.equals(encodedImage, photo.encodedImage)
                && Objects.equals(metadata, photo.metadata);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rotationDegrees, metadata);
        result = 31 * result + Arrays.hashCode(encodedImage);
        return result;
    }

}
