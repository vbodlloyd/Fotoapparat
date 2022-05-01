package io.fotoapparat.lens;

import android.support.annotation.Nullable;

public class CaptureMetadata {
    @Nullable
    private final Float lensFocusDistance;

    public CaptureMetadata(@Nullable final Float lensFocusDistance) {
        this.lensFocusDistance = lensFocusDistance;
    }

    @Nullable
    public Float getLensFocusDistance() {
        return lensFocusDistance;
    }

    @Override
    public String toString() {
        return "CaptureMetadata{" +
                "lensFocusDistance=" + lensFocusDistance +
                '}';
    }
}
