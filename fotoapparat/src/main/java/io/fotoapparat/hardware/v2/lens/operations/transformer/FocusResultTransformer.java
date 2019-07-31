package io.fotoapparat.hardware.v2.lens.operations.transformer;

import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import io.fotoapparat.lens.FocusResult;
import io.fotoapparat.result.transformer.Transformer;

/**
 * Transforms a {@link CaptureResult} into a {@link FocusResult}.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FocusResultTransformer implements Transformer<CaptureResult, FocusResult> {

    @Override
    public FocusResult transform(CaptureResult input) {
        Integer autoFocusState = input.get(CaptureResult.CONTROL_AF_STATE);

        boolean lockSucceeded = autoFocusState != null && isFocusLocked(autoFocusState);
        boolean needsExposureMeasurement = needsExposureMeasurement(input);
        Log.d("Fotoapparat", "focus result: "+lockSucceeded +" hasToMeasureAgain: "+ needsExposureMeasurement);
        return new FocusResult(lockSucceeded, needsExposureMeasurement);
    }

    private boolean needsExposureMeasurement(CaptureResult input) {
        Integer autoExposure = input.get(CaptureResult.CONTROL_AE_STATE);
        Boolean isLocked = input.get(CaptureResult.CONTROL_AE_LOCK);
        MeteringRectangle[] areas = input.get(CaptureResult.CONTROL_AE_REGIONS);
        Log.d("Fotoapparat","exposure status is :" + isLocked +" "+ areas[0] +" "+ autoExposure);
        return autoExposure == null || !isExposureValuesConverged(autoExposure);
    }

    private boolean isExposureValuesConverged(Integer autoExposure) {
        return autoExposure == CaptureResult.CONTROL_AE_STATE_CONVERGED || autoExposure == CaptureResult.CONTROL_AE_STATE_PRECAPTURE;
    }

    private boolean isFocusLocked(Integer autoFocusState) {
        return autoFocusState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                || autoFocusState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED;
    }
}
