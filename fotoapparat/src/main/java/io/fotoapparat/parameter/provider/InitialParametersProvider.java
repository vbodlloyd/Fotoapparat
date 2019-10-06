package io.fotoapparat.parameter.provider;

import java.util.Collection;

import io.fotoapparat.hardware.CameraDevice;
import io.fotoapparat.hardware.Capabilities;
import io.fotoapparat.hardware.operators.CapabilitiesOperator;
import io.fotoapparat.parameter.AntiBandingMode;
import io.fotoapparat.parameter.Flash;
import io.fotoapparat.parameter.FocusMode;
import io.fotoapparat.parameter.Parameters;
import io.fotoapparat.parameter.Size;
import io.fotoapparat.parameter.factory.ParametersFactory;
import io.fotoapparat.parameter.range.Range;
import io.fotoapparat.parameter.selector.SelectorFunction;
import io.fotoapparat.parameter.selector.Selectors;

import static io.fotoapparat.parameter.Parameters.combineParameters;
import static io.fotoapparat.parameter.selector.AspectRatioSelectors.aspectRatio;
import static java.util.Arrays.asList;

/**
 * Provides initial {@link Parameters} for {@link CameraDevice}.
 */
public class InitialParametersProvider {

    private final InitialParametersValidator parametersValidator;
    private final CapabilitiesOperator capabilitiesOperator;
    private final SelectorFunction<Collection<Size>, Size> photoSizeSelector;
    private final SelectorFunction<Collection<Size>, Size> previewSizeSelector;
    private final SelectorFunction<Collection<AntiBandingMode>, AntiBandingMode> antiBandingModeSelector;
    private final SelectorFunction<Collection<FocusMode>, FocusMode> focusModeSelector;
    private final SelectorFunction<Collection<Flash>, Flash> flashSelector;
    private final SelectorFunction<Collection<Range<Integer>>, Range<Integer>> previewFpsRangeSelector;
    private final SelectorFunction<Range<Integer>, Integer> sensorSensitivitySelector;
    private final int jpegQuality;
    private final boolean centerExposure;
    private final boolean reinitFlash;

    public InitialParametersProvider(CapabilitiesOperator capabilitiesOperator,
                                     SelectorFunction<Collection<Size>, Size> photoSizeSelector,
                                     SelectorFunction<Collection<Size>, Size> previewSizeSelector,
                                     SelectorFunction<Collection<AntiBandingMode>, AntiBandingMode> antiBandingModeSelector,
                                     SelectorFunction<Collection<FocusMode>, FocusMode> focusModeSelector,
                                     SelectorFunction<Collection<Flash>, Flash> flashSelector,
                                     SelectorFunction<Collection<Range<Integer>>, Range<Integer>> previewFpsRangeSelector,
                                     SelectorFunction<Range<Integer>, Integer> sensorSensitivitySelector,
                                     int jpegQuality,
                                     boolean centerExposure,
                                     boolean reinitFlash,
                                     InitialParametersValidator parametersValidator) {
        this.capabilitiesOperator = capabilitiesOperator;
        this.photoSizeSelector = photoSizeSelector;
        this.previewSizeSelector = previewSizeSelector;
        this.antiBandingModeSelector = antiBandingModeSelector;
        this.focusModeSelector = focusModeSelector;
        this.flashSelector = flashSelector;
        this.previewFpsRangeSelector = previewFpsRangeSelector;
        this.sensorSensitivitySelector = sensorSensitivitySelector;
        this.jpegQuality = jpegQuality;
        this.parametersValidator = parametersValidator;
        this.centerExposure = centerExposure;
        this.reinitFlash = reinitFlash;
    }

    /**
     * @return function which selects a valid preview size based on current picture size.
     */
    static SelectorFunction<Collection<Size>, Size> validPreviewSizeSelector(Size photoSize,
                                                                             SelectorFunction<Collection<Size>, Size> original) {
        return Selectors
                .firstAvailable(
                        previewWithSameAspectRatio(photoSize, original),
                        original
                );
    }

    private static SelectorFunction<Collection<Size>, Size> previewWithSameAspectRatio(Size photoSize,
                                                                                       SelectorFunction<Collection<Size>, Size> original) {
        return aspectRatio(
                photoSize.getAspectRatio(),
                original
        );
    }

    /**
     * @return {@link Parameters} which will be used by {@link CameraDevice} on start-up.
     */
    public Parameters initialParameters() {
        Capabilities capabilities = capabilitiesOperator.getCapabilities();

        Parameters parameters = combineParameters(asList(
                pictureSizeParameters(capabilities),
                previewSizeParameters(capabilities),
                antiBandingModeParameters(capabilities),
                focusModeParameters(capabilities),
                flashModeParameters(capabilities),
                previewFpsRange(capabilities),
                sensorSensitivity(capabilities),
                jpegQuality(),
                centerExposure(),
                reinitFlash()
        ));

        parametersValidator.validate(parameters);

        return parameters;
    }

    private Parameters flashModeParameters(Capabilities capabilities) {
        return ParametersFactory.selectFlashMode(
                capabilities,
                flashSelector
        );
    }

    private Parameters antiBandingModeParameters(Capabilities capabilities) {
        return ParametersFactory.selectAntiBandingMode(
                capabilities,
                antiBandingModeSelector
        );
    }

    private Parameters focusModeParameters(Capabilities capabilities) {
        return ParametersFactory.selectFocusMode(
                capabilities,
                focusModeSelector
        );
    }

    private Parameters previewSizeParameters(Capabilities capabilities) {
        return ParametersFactory.selectPreviewSize(
                capabilities,
                validPreviewSizeSelector(
                        photoSize(capabilities),
                        previewSizeSelector
                )
        );
    }

    private Parameters pictureSizeParameters(Capabilities capabilities) {
        return ParametersFactory.selectPictureSize(
                capabilities,
                photoSizeSelector
        );
    }

    private Size photoSize(Capabilities capabilities) {
        return photoSizeSelector.select(
                capabilities.supportedPictureSizes()
        );
    }

    private Parameters previewFpsRange(Capabilities capabilities) {
        return ParametersFactory.selectPreviewFpsRange(
                capabilities,
                previewFpsRangeSelector
        );
    }

    private Parameters sensorSensitivity(Capabilities capabilities) {
        return ParametersFactory.selectSensorSensitivity(
                capabilities,
                sensorSensitivitySelector
        );
    }

    private Parameters jpegQuality() {
        return ParametersFactory.selectJpegQuality(
                jpegQuality
        );
    }

    private Parameters centerExposure(){
        return ParametersFactory.selectCenterExposure(centerExposure);
    }

    private Parameters reinitFlash(){
        return ParametersFactory.selectReinitFlash(reinitFlash);
    }

}
