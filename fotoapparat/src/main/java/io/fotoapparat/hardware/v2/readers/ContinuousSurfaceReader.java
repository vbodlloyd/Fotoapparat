package io.fotoapparat.hardware.v2.readers;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Surface;

import java.nio.ByteBuffer;

import io.fotoapparat.hardware.v2.CameraThread;
import io.fotoapparat.hardware.v2.parameters.ParametersProvider;
import io.fotoapparat.hardware.v2.stream.OnImageAcquiredObserver;
import io.fotoapparat.parameter.Size;
import io.fotoapparat.util.YUVUtil;

/**
 * Creates a {@link Surface} which can capture continuous events (several frames).
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ContinuousSurfaceReader
        implements OnImageAcquiredObserver, ImageReader.OnImageAvailableListener {

    private final ParametersProvider parametersProvider;
    private final CameraThread cameraThread;
    private ImageReader imageReader;
    private OnFrameAcquiredListener listener;

    public ContinuousSurfaceReader(ParametersProvider parametersProvider, CameraThread cameraThread) {
        this.parametersProvider = parametersProvider;
        this.cameraThread = cameraThread;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireNextImage();
        Image.Plane[] planes = image.getPlanes();

        if (planes.length > 0) {
            byte[] bytes = YUVUtil.yuvToGrayscaleRGB(image);

            if (listener != null) {
                listener.onFrameAcquired(bytes);
            }
        }
        image.close();
    }

    /**
     * Returns a {@link Surface} which can be used as a target for continuous capture events.
     *
     * @return the new Surface
     */
    public Surface getSurface() {
        if (imageReader == null) {
            createImageReader();
        }
        return imageReader.getSurface();
    }

    private void createImageReader() {
        Size previewSize = parametersProvider.getPreviewSize();

        imageReader = ImageReader
                .newInstance(
                        previewSize.width,
                        previewSize.height,
                        ImageFormat.YUV_420_888,
                        1
                );

        imageReader.setOnImageAvailableListener(
                this,
                cameraThread.createHandler()
        );
    }

    @Override
    public void setListener(OnFrameAcquiredListener listener) {
        this.listener = listener;
    }
}
