package io.fotoapparat.util;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.nio.ByteBuffer;

/**
 * Utility class about the YUV format
 */
public class YUVUtil {
    /**
     * Retrieves a byte array representing a given YUV {@link Image}.
     * @param image The YUV {@link Image}
     * @return The byte array
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static byte[] imageToByte(final Image image) {
        final Rect crop = image.getCropRect();
        final int width = crop.width();
        final int height = crop.height();

        final Image.Plane[] planes = image.getPlanes();
        final byte[] rowData = new byte[planes[0].getRowStride()];
        final int bufferSize = width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8;
        final ByteBuffer output = ByteBuffer.allocateDirect(bufferSize);

        int channelOffset = 0;
        int outputStride = 0;

        for (int planeIndex = 0; planeIndex < 3; planeIndex++) {
            if (planeIndex == 0) {
                channelOffset = 0;
                outputStride = 1;
            } else if (planeIndex == 1) {
                channelOffset = width * height + 1;
                outputStride = 2;
            } else if (planeIndex == 2) {
                channelOffset = width * height;
                outputStride = 2;
            }

            final ByteBuffer buffer = planes[planeIndex].getBuffer();
            final int rowStride = planes[planeIndex].getRowStride();
            final int pixelStride = planes[planeIndex].getPixelStride();

            final int shift = (planeIndex == 0) ? 0 : 1;
            final int widthShifted = width >> shift;
            final int heightShifted = height >> shift;

            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));

            for (int row = 0; row < heightShifted; row++) {
                final int length;

                if (pixelStride == 1 && outputStride == 1) {
                    length = widthShifted;
                    buffer.get(output.array(), channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (widthShifted - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);

                    for (int col = 0; col < widthShifted; col++) {
                        output.array()[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }

                if (row < heightShifted - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }

        return output.array();
    }

    /**
     * Converts a YUV {@link Image} into a grayscale ARGB 8888 byte array.
     * @param image The YUV {@link Image}
     * @return A byte array containing a grayscale image in the ARGB 8888 format.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static byte[] yuvToGrayscaleRGB(final Image image) {
        final int planeIndex = 0; // Grayscale: Only the Y channel

        final Rect crop = image.getCropRect();
        final Image.Plane[] planes = image.getPlanes();
        final int rowStride = planes[planeIndex].getRowStride();
        final ByteBuffer buffer = planes[planeIndex].getBuffer();

        return yuvToGrayscaleRGB(buffer, rowStride, crop);
    }

    /**
     * Converts a YUV {@link Image} into a grayscale ARGB 8888 byte array.
     *
     * @param yBuffer The Y channel {@link ByteBuffer} of the YUV {@link Image}.
     * @param rowStride The row stride of the Y channel
     * @param crop The YUV {@link Image} crop {@link Rect}
     * @return A byte array containing a grayscale image in the ARGB 8888 format.
     */
    public static byte[] yuvToGrayscaleRGB(final ByteBuffer yBuffer, final int rowStride, final Rect crop) {
        final int outputStride = 4; // ARGB_8888: 4 Bytes per pixel

        final int width = crop.width();
        final int height = crop.height();

        final byte[] rowData = new byte[rowStride];
        final int outputSize = width * height * outputStride;
        final byte[] output = new byte[outputSize];


        // Pixel stride always 1 for Y
        yBuffer.position(rowStride * crop.top + crop.left);

        int outputOffset = 0;
        byte b;
        for (int row = 0; row < height; row++) {
            yBuffer.get(rowData, 0, width);

            for (int col = 0; col < width; col++) {
                b = rowData[col];
                output[outputOffset] = b;
                output[outputOffset + 1] = b;
                output[outputOffset + 2] = b;
                output[outputOffset + 3] = (byte) 0xFF;
                outputOffset += outputStride;
            }

            if (row < height - 1) {
                yBuffer.position(yBuffer.position() + rowStride - width);
            }
        }

        return output;
    }
}
