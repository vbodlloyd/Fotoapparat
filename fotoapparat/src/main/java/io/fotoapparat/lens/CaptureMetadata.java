package io.fotoapparat.lens;

import android.support.annotation.Nullable;

public class CaptureMetadata {
    @Nullable
    private final Float lensFocusDistance;
    @Nullable
    private final Integer sensorSensitivityISO;
    @Nullable
    private final Long sensorExposureTimeNs;
    @Nullable
    private final Float lensAperture;

    public CaptureMetadata(@Nullable final Float lensFocusDistance,
                           @Nullable final Integer sensorSensitivityISO,
                           @Nullable final Long sensorExposureTimeNs,
                           @Nullable final Float lensAperture) {
        this.lensFocusDistance = lensFocusDistance;
        this.sensorSensitivityISO = sensorSensitivityISO;
        this.sensorExposureTimeNs = sensorExposureTimeNs;
        this.lensAperture = lensAperture;
    }

    @Nullable
    public Float getLensFocusDistance() {
        return lensFocusDistance;
    }

    @Nullable
    public Integer getSensorSensitivityISO() {
        return sensorSensitivityISO;
    }

    @Nullable
    public Long getSensorExposureTimeNs() {
        return sensorExposureTimeNs;
    }

    @Nullable
    public Float getLensAperture() {
        return lensAperture;
    }

    @Override
    public String toString() {
        return "CaptureMetadata{" +
                "lensFocusDistance=" + lensFocusDistance +
                ", sensorSensitivityISO=" + sensorSensitivityISO +
                ", sensorExposureTimeNs=" + sensorExposureTimeNs +
                ", lensAperture=" + lensAperture +
                '}';
    }
}
