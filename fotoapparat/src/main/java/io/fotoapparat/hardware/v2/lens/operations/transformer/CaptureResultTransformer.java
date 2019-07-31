package io.fotoapparat.hardware.v2.lens.operations.transformer;

import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import io.fotoapparat.lens.CaptureResultState;
import io.fotoapparat.lens.ExposureResultState;
import io.fotoapparat.result.transformer.Transformer;

/**
 * Transforms a {@link CaptureResult} into a {@link ExposureResultState}.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureResultTransformer implements Transformer<CaptureResult, CaptureResultState> {

    @Override
    public CaptureResultState transform(CaptureResult input) {
        Integer autoExposure = input.get(CaptureResult.CONTROL_AE_STATE);
        Boolean isLocked = input.get(CaptureResult.CONTROL_AE_LOCK);
        MeteringRectangle[] areas = input.get(CaptureResult.CONTROL_AE_REGIONS);

        Log.d("Fotoapparat","exposure status after capture is :" + isLocked +" "+ areas[0] +" "+ autoExposure + "aeras.size: "+areas.length);
        return CaptureResultState.SUCCESS;
    }

}
