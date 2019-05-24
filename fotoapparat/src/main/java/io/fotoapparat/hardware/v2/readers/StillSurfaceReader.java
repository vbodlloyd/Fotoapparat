package io.fotoapparat.hardware.v2.readers;

import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

import io.fotoapparat.hardware.v2.CameraThread;
import io.fotoapparat.hardware.v2.parameters.ParametersProvider;
import io.fotoapparat.parameter.Size;

/**
 * Creates a {@link Surface} which can capture single events.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class StillSurfaceReader {

    private final CameraThread cameraThread;
    private final ParametersProvider parametersProvider;
    private ImageReader imageReader;

    public StillSurfaceReader(ParametersProvider parametersProvider, CameraThread cameraThread) {
        this.parametersProvider = parametersProvider;
        this.cameraThread = cameraThread;
    }

    /**
     * Returns a {@link Surface} which can be used as a target for still capture events.
     *
     * @return the new Surface
     */
    public Surface getSurface() {
        if (imageReader == null) {
            createImageReader();
        }
        return imageReader.getSurface();
    }

    /**
     * Returns the next available Image as a byte array.
     *
     * @return the Image as byte array.
     */
    public byte[] getPhotoBytes() {
        ImageCaptureAction imageCaptureAction = new ImageCaptureAction(imageReader, cameraThread);

        return imageCaptureAction.getPhoto();
    }

    private void createImageReader() {
        Size largestSize = parametersProvider.getStillCaptureSize();

        imageReader = ImageReader
                .newInstance(
                        largestSize.width,
                        largestSize.height,
                        ImageFormat.YUV_420_888,
                        1
                );

    }

    private static class ImageCaptureAction implements ImageReader.OnImageAvailableListener {

        private final CountDownLatch countDownLatch = new CountDownLatch(1);
        private final ImageReader imageReader;
        private byte[] bytes;

        private ImageCaptureAction(ImageReader imageReader, CameraThread cameraThread) {
            this.imageReader = imageReader;
            imageReader.setOnImageAvailableListener(
                    this,
                    cameraThread.createHandler()
            );
        }

        private byte[] getPhoto() {
            Image image = imageReader.acquireLatestImage();
            Log.d("FORMAT", "format = " +imageReader.getImageFormat() );
            if (image != null) {
                removeListener();
                Image.Plane[] planes = image.getPlanes();

                if (planes.length > 0) {
                    return YUV_420_888toNV21(image);
                }
                return imageToBytes(image);
            }

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                // Do nothing
            }

            return bytes;
        }

        private static byte[] YUV_420_888toNV21(Image image) {
            byte[] nv21;
            ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
            ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
            ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            nv21 = new byte[ySize + uSize + vSize];

            //U and V are swapped
            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);
            uBuffer.get(nv21, ySize + vSize, uSize);

            return nv21;
        }

        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            try {
                image = reader.acquireLatestImage();
                Log.d("FORMAT", "format = " +reader.getImageFormat() );

                //bytes = imageToBytes(image);
                Image.Plane[] planes = image.getPlanes();

                if (planes.length > 0) {
                    bytes = YUV_420_888toNV21(image);
                }
            } catch ( IllegalStateException ex){
                Log.e("FotoApparat","error during read of an image ",ex);
            } finally {
                if(image != null){
                    image.close();
                }
            }
            removeListener();
            countDownLatch.countDown();
        }

        private byte[] imageToBytes(Image image) {
            Image.Plane[] planes = image.getPlanes();

            ByteBuffer buffer = planes[0].getBuffer();

            byte[] result = new byte[buffer.remaining()];
            buffer.get(result);

            image.close();

            return result;
        }

        private void removeListener() {
            imageReader.setOnImageAvailableListener(null, null);
        }
    }

}
