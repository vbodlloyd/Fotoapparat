package io.fotoapparat.hardware;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Set;

import io.fotoapparat.parameter.AntiBandingMode;
import io.fotoapparat.parameter.Flash;
import io.fotoapparat.parameter.FocusMode;
import io.fotoapparat.parameter.Size;
import io.fotoapparat.parameter.range.Range;
import io.fotoapparat.parameter.range.Ranges;

/**
 * Capabilities of camera hardware.
 */
public class Capabilities {

    @NonNull
    private final Set<Size> photoSizes;
    @NonNull
    private final Set<Size> previewSizes;
    @NonNull
    private final Set<AntiBandingMode> antiBandingModes;
    @NonNull
    private final Set<FocusMode> focusModes;
    @NonNull
    private final Set<Flash> flashModes;
    @NonNull
    private final Set<Range<Integer>> previewFpsRanges;
    @NonNull
    private final Range<Integer> sensorSensitivityRange;
    @Nullable
    private final Float lensMinFocusDistance;

    private final boolean zoomSupported;

    public Capabilities(@NonNull Set<Size> photoSizes,
                        @NonNull Set<Size> previewSizes,
                        @NonNull Set<AntiBandingMode> antiBandingModes,
                        @NonNull Set<FocusMode> focusModes,
                        @NonNull Set<Flash> flashModes,
                        @NonNull Set<Range<Integer>> previewFpsRanges,
                        @NonNull Range<Integer> sensorSensitivityRange,
                        boolean zoomSupported,
                        @Nullable final Float lensMinFocusDistance) {
        this.photoSizes = photoSizes;
        this.previewSizes = previewSizes;
        this.antiBandingModes = antiBandingModes;
        this.focusModes = focusModes;
        this.flashModes = flashModes;
        this.previewFpsRanges = previewFpsRanges;
        this.sensorSensitivityRange = sensorSensitivityRange;
        this.zoomSupported = zoomSupported;
        this.lensMinFocusDistance = lensMinFocusDistance;
    }

    /**
     * @return Empty {@link Capabilities}.
     */
    public static Capabilities empty() {
        return new Capabilities(
                Collections.<Size>emptySet(),
                Collections.<Size>emptySet(),
                Collections.<AntiBandingMode>emptySet(),
                Collections.<FocusMode>emptySet(),
                Collections.<Flash>emptySet(),
                Collections.<Range<Integer>>emptySet(),
                Ranges.<Integer>emptyRange(),
                false,
                null
        );
    }

    /**
     * @return list of supported picture sizes.
     */
    public Set<Size> supportedPictureSizes() {
        return photoSizes;
    }

    /**
     * @return list of supported preview sizes;
     */
    public Set<Size> supportedPreviewSizes() {
        return previewSizes;
    }

    /**
     * @return list of supported anti banding modes.
     */
    public Set<AntiBandingMode> supportedAntiBandingModes() {
        return antiBandingModes;
    }
    /**
     * @return list of supported focus modes.
     */
    public Set<FocusMode> supportedFocusModes() {
        return focusModes;
    }

    /**
     * @return list of supported flash firing modes.
     */
    public Set<Flash> supportedFlashModes() {
        return flashModes;
    }

    /**
     * @return list of supported preview fps ranges.
     */
    public Set<Range<Integer>> supportedPreviewFpsRanges() {
        return previewFpsRanges;
    }

    /**
     * @return supported range of the sensor's sensitivity.
     */
    public Range<Integer> supportedSensorSensitivityRange() {
        return sensorSensitivityRange;
    }

    /**
     * @return {@code true} if zoom feature is supported. {@code false} if it is not supported.
     */
    public boolean isZoomSupported() {
        return zoomSupported;
    }

    @Nullable
    public Float getLensMinFocusDistance() {
        return lensMinFocusDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Capabilities)) return false;

        Capabilities that = (Capabilities) o;

        return zoomSupported == that.zoomSupported
                && photoSizes.equals(that.photoSizes)
                && previewSizes.equals(that.previewSizes)
                && focusModes.equals(that.focusModes)
                && flashModes.equals(that.flashModes)
                && previewFpsRanges.equals(that.previewFpsRanges)
                && sensorSensitivityRange.equals(that.sensorSensitivityRange);
    }

    @Override
    public int hashCode() {
        int result = photoSizes.hashCode();
        result = 31 * result + previewSizes.hashCode();
        result = 31 * result + focusModes.hashCode();
        result = 31 * result + flashModes.hashCode();
        result = 31 * result + previewFpsRanges.hashCode();
        result = 31 * result + sensorSensitivityRange.hashCode();
        result = 31 * result + (isZoomSupported() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Capabilities{" +
                "photoSizes=" + photoSizes +
                ", previewSizes=" + previewSizes +
                ", focusModes=" + focusModes +
                ", flashModes=" + flashModes +
                ", previewFpsRanges=" + previewFpsRanges +
                ", supportedSensorSensitivityRange=" + sensorSensitivityRange +
                ", zoomSupported=" + zoomSupported +
                '}';
    }

}
