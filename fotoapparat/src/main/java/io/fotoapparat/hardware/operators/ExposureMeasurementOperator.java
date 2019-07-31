package io.fotoapparat.hardware.operators;

import io.fotoapparat.result.MeteringResult;

/**
 * Measures the exposure.
 */
public interface ExposureMeasurementOperator {

    /**
     * Measures the exposure. This is a blocking operation which returns when measurement completes.
     */
    MeteringResult measureExposure();

}
