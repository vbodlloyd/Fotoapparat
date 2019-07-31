package io.fotoapparat.hardware.v2.lens.executors;

import android.util.Log;

import io.fotoapparat.hardware.operators.ExposureMeasurementOperator;
import io.fotoapparat.hardware.v2.lens.operations.LensOperation;
import io.fotoapparat.hardware.v2.lens.operations.LensOperationsFactory;
import io.fotoapparat.lens.ExposureResultState;
import io.fotoapparat.result.MeteringResult;

/**
 * Performs an exposure gathering routine.
 */
@SuppressWarnings("NewApi")
public class ExposureGatheringExecutor implements ExposureMeasurementOperator {

    private final LensOperationsFactory lensOperationsFactory;

    public ExposureGatheringExecutor(LensOperationsFactory lensOperationsFactory) {
        this.lensOperationsFactory = lensOperationsFactory;
    }

    @Override
    public MeteringResult measureExposure() {
        LensOperation<ExposureResultState> lensOperation = lensOperationsFactory.createExposureGatheringOperation();
        Log.d("Fotoapparat","measureExposure operation will be called");
        ExposureResultState result = lensOperation.call();
        Log.d("Fotoapparat","measureExposure operation has been called: "+ result);
        if (result == ExposureResultState.SUCCESS) {
            return MeteringResult.success();
        }
        return MeteringResult.failure();
    }
}
