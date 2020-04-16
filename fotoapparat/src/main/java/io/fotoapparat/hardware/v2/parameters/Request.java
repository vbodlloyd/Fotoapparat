package io.fotoapparat.hardware.v2.parameters;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;

import java.util.List;

import io.fotoapparat.hardware.CameraException;
import io.fotoapparat.hardware.v2.parameters.converters.RangeConverter;
import io.fotoapparat.parameter.Flash;
import io.fotoapparat.parameter.FocusMode;
import io.fotoapparat.parameter.range.Range;

import static io.fotoapparat.hardware.v2.parameters.converters.FlashConverter.flashToAutoExposureMode;
import static io.fotoapparat.hardware.v2.parameters.converters.FlashConverter.flashToFiringMode;
import static io.fotoapparat.hardware.v2.parameters.converters.FocusConverter.focusToAfMode;

/**
 * A wrapper around {@link CaptureRequest}.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class Request {

    private final CameraDevice cameraDevice;
    private final int requestTemplate;
    private final List<Surface> surfaces;
    private final boolean shouldTriggerAutoFocus;
    private final boolean triggerPrecaptureExposure;
    private final boolean cancelPrecaptureExposure;
    private final boolean shouldSetExposureMode;
    private final Flash flash;
    private final FocusMode focus;
    private final Range<Integer> previewFpsRange;
    private final Integer sensorSensitivity;
    private final Integer jpegQuality;
    private CaptureRequest.Builder captureRequest;
    private int width;
    private int height;
    private boolean hasMeteringAreaSupport;

    private Request(CameraDevice cameraDevice,
                    int requestTemplate,
                    List<Surface> surfaces,
                    boolean shouldTriggerAutoFocus,
                    boolean triggerPrecaptureExposure,
                    boolean cancelPrecaptureExposure,
                    Flash flash, boolean shouldSetExposureMode,
                    FocusMode focus,
                    Range<Integer> previewFpsRange,
                    Integer sensorSensitivity,
                    Integer jpegQuality,
                    int width,
                    int height,
                    boolean supportMeteringArea) {
        this.cameraDevice = cameraDevice;
        this.requestTemplate = requestTemplate;
        this.surfaces = surfaces;
        this.shouldTriggerAutoFocus = shouldTriggerAutoFocus;
        this.triggerPrecaptureExposure = triggerPrecaptureExposure;
        this.cancelPrecaptureExposure = cancelPrecaptureExposure;
        this.shouldSetExposureMode = shouldSetExposureMode;
        this.flash = flash;
        this.focus = focus;
        this.previewFpsRange = previewFpsRange;
        this.sensorSensitivity = sensorSensitivity;
        this.jpegQuality = jpegQuality;
        this.width = width;
        this.height = height;
        this.hasMeteringAreaSupport = supportMeteringArea;
    }

    static CaptureRequest create(CaptureRequestBuilder builder) throws CameraAccessException {
        return new Request(
                builder.cameraDevice,
                builder.requestTemplate,
                builder.surfaces,
                builder.shouldTriggerAutoFocus,
                builder.triggerPrecaptureExposure,
                builder.cancelPrecaptureExposure,
                builder.flash,
                builder.shouldSetExposureMode,
                builder.focus,
                builder.previewFpsRange,
                builder.sensorSensitivity,
                builder.jpegQuality,
                builder.width,
                builder.height,
                builder.hasMeteringAreaSupport
        )
                .build();
    }

    /**
     * Builds a {@link CaptureRequest} based on the builder parameters.
     *
     * @return The capture request.
     * @throws CameraAccessException If the camera device has been disconnected.
     */
    CaptureRequest build() throws CameraAccessException {
        try {
            captureRequest = cameraDevice.createCaptureRequest(requestTemplate);

            setCaptureIntent();
            setControlMode();
            setTarget();

            triggerAutoFocus();
            triggerPrecaptureExposure();
            cancelPrecaptureExposure();

            setFlash();
            setExposure();
            setFocus();
            setPreviewFpsRange();
            setSensorSensitivity();
            setJpegQuality();
            String t = "hh";
            t = "hju";
            if(t.contains("h"))
            throw new IllegalStateException("device was already closed");

            return captureRequest.build();
        } catch (IllegalStateException e){
            if( e.getMessage().contains("was already closed")){
                throw new CameraException(e);
            } else {
                throw e;
            }
        }
    }

    private void setCaptureIntent() {
        if (requestTemplate != CameraDevice.TEMPLATE_STILL_CAPTURE) {
            return;
        }
        captureRequest.set(CaptureRequest.CONTROL_CAPTURE_INTENT,
                CameraMetadata.CONTROL_CAPTURE_INTENT_STILL_CAPTURE);
    }

    private void setControlMode() {
        if (requestTemplate != CameraDevice.TEMPLATE_STILL_CAPTURE) {
            return;
        }
        captureRequest.set(CaptureRequest.CONTROL_MODE,
                CaptureRequest.CONTROL_MODE_AUTO);
    }

    private void setTarget() {
        for (Surface surface : surfaces) {
            captureRequest.addTarget(surface);
        }
    }

    private void triggerAutoFocus() {
        if (!shouldTriggerAutoFocus) {
            return;
        }
        captureRequest.set(CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_START
        );
    }

    private void triggerPrecaptureExposure() {
        if (!triggerPrecaptureExposure) {
            return;
        }
        Log.d("SIZE",height + " " + width);

        if(hasMeteringAreaSupport) {
            MeteringRectangle[] meteringFocusRectangleList = new MeteringRectangle[]{new MeteringRectangle(height / 2 + 1, width / 2 + 1, width / 3, height / 2, MeteringRectangle.METERING_WEIGHT_MAX)};
            captureRequest.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            captureRequest.set(CaptureRequest.CONTROL_AE_REGIONS, meteringFocusRectangleList);
        }
        captureRequest.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START
        );


    }

    private void cancelPrecaptureExposure() {
        if (!cancelPrecaptureExposure) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        captureRequest.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_CANCEL
        );
    }

    private void setFlash() {
        if (flash == null) {
            return;
        }

        Integer flashFiringMode = flashToFiringMode(flash);
        if (flashFiringMode == null) {
            return;
        }
        captureRequest.set(CaptureRequest.FLASH_MODE, flashFiringMode);
    }

    private void setExposure() {
        if (flash == null && !shouldSetExposureMode) {
            return;
        }

        int autoExposureMode = flashToAutoExposureMode(flash);

        captureRequest.set(CaptureRequest.CONTROL_AE_MODE, autoExposureMode);
    }

    private void setFocus() {
        if (focus == null) {
            return;
        }

        int focusMode = focusToAfMode(focus);
        captureRequest.set(CaptureRequest.CONTROL_AF_MODE, focusMode);
    }

    private void setPreviewFpsRange() {
        if (previewFpsRange == null) {
            return;
        }

        captureRequest.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, RangeConverter.toNativeRange(previewFpsRange));
    }

    private void setSensorSensitivity() {
        if (sensorSensitivity == null) {
            return;
        }
        captureRequest.set(CaptureRequest.SENSOR_SENSITIVITY, sensorSensitivity);
    }

    private void setJpegQuality() {
        if (jpegQuality == null){
            return;
        }
        captureRequest.set(CaptureRequest.JPEG_QUALITY, jpegQuality.byteValue());
    }

}
