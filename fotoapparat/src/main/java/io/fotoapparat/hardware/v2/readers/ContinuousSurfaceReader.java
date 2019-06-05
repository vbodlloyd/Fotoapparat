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
            byte[] bytes = YUV420toNV21(image);

            if (listener != null) {
                listener.onFrameAcquired(bytes);
            }
        }
        image.close();
    }

    private static byte[] YUV420toNV21(Image image) {
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];

        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    channelOffset = width * height + 1;
                    outputStride = 2;
                    break;
                case 2:
                    channelOffset = width * height;
                    outputStride = 2;
                    break;
            }

            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();

            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }
        return data;
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
