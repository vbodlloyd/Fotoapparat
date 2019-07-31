package io.fotoapparat.hardware.v2.lens.operations.transformer;

import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import io.fotoapparat.lens.ExposureResultState;
import io.fotoapparat.result.transformer.Transformer;

/**
 * Transforms a {@link CaptureResult} into a {@link ExposureResultState}.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ExposureResultTransformer implements Transformer<CaptureResult, ExposureResultState> {

    @Override
    public ExposureResultState transform(CaptureResult input) {
        Integer autoExposure = input.get(CaptureResult.CONTROL_AE_STATE);
        Boolean isLocked = input.get(CaptureResult.CONTROL_AE_LOCK);
        MeteringRectangle[] areas = input.get(CaptureResult.CONTROL_AE_REGIONS);
        Log.d("Fotoapparat","exposure after measure status is :" + isLocked +" "+ areas[0] +" "+ autoExposure + "areas.size: "+ areas.length);

        if (autoExposure != null && isExposureValuesConverged(autoExposure)) {
            return ExposureResultState.SUCCESS;
        }
        return ExposureResultState.FAILURE;
    }

    private boolean isExposureValuesConverged(Integer autoExposure) {
        return autoExposure == CaptureResult.CONTROL_AE_STATE_CONVERGED || autoExposure == CaptureResult.CONTROL_AE_STATE_PRECAPTURE;
    }
}
