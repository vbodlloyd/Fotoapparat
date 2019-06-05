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

    private byte[] imageToByte(final Image image)
    {
        final Rect crop   = image.getCropRect();
        final int  width  = crop.width();
        final int  height = crop.height();

        final Image.Plane[] planes     = image.getPlanes();
        final byte[]        rowData    = new byte[planes[0].getRowStride()];
        final int           bufferSize = width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8;
        final ByteBuffer    output     = ByteBuffer.allocateDirect(bufferSize);

        int channelOffset = 0;
        int outputStride = 0;

        for (int planeIndex = 0; planeIndex < 3; planeIndex++)
        {
            if (planeIndex == 0)
            {
                channelOffset = 0;
                outputStride = 1;
            }
            else if (planeIndex == 1)
            {
                channelOffset = width * height + 1;
                outputStride = 2;
            }
            else if (planeIndex == 2)
            {
                channelOffset = width * height;
                outputStride = 2;
            }

            final ByteBuffer buffer      = planes[planeIndex].getBuffer();
            final int        rowStride   = planes[planeIndex].getRowStride();
            final int        pixelStride = planes[planeIndex].getPixelStride();

            final int shift         = (planeIndex == 0) ? 0 : 1;
            final int widthShifted  = width >> shift;
            final int heightShifted = height >> shift;

            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));

            for (int row = 0; row < heightShifted; row++)
            {
                final int length;

                if (pixelStride == 1 && outputStride == 1)
                {
                    length = widthShifted;
                    buffer.get(output.array(), channelOffset, length);
                    channelOffset += length;
                }
                else
                {
                    length = (widthShifted - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);

                    for (int col = 0; col < widthShifted; col++)
                    {
                        output.array()[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }

                if (row < heightShifted - 1)
                {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }

        return output.array();
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireNextImage();
        Image.Plane[] planes = image.getPlanes();

        if (planes.length > 0) {
            byte[] bytes = imageToByte(image);

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
