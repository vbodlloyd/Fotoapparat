package io.fotoapparat.lens;

import android.support.annotation.Nullable;

/**
 * The result of an attempt to capture a photo.
 */
public class CaptureResultState {
    private final boolean success;

    @Nullable
    private final CaptureMetadata metadata;

    private CaptureResultState(final boolean success, @Nullable final CaptureMetadata metadata) {
        this.success = success;
        this.metadata = metadata;
    }

    public static CaptureResultState Success(final CaptureMetadata metadata) {
        return new CaptureResultState(true, metadata);
    }

    public static CaptureResultState Failure() {
        return new CaptureResultState(false, null);
    }

    public boolean isSuccess() {
        return success;
    }

    @Nullable
    public CaptureMetadata getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "CaptureResultState." + (success ? "Success(metadata=" + metadata + ')' : "Failure");
    }
}
