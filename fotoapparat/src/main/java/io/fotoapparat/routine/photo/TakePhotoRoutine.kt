package io.fotoapparat.routine.photo

import io.fotoapparat.Fotoapparat
import io.fotoapparat.exception.camera.CameraException
import io.fotoapparat.hardware.CameraDevice
import io.fotoapparat.hardware.Device
import io.fotoapparat.result.Photo
import kotlinx.coroutines.runBlocking

/**
 * Takes a photo.
 */
internal fun Device.takePhoto(pictureMode: Fotoapparat.PictureMode): Photo = runBlocking {
    val cameraDevice = awaitSelectedCamera()

    when(pictureMode){
        Fotoapparat.PictureMode.STANDARD ->  cameraDevice.takePhoto().also { cameraDevice.startPreviewSafely() }
        Fotoapparat.PictureMode.STILL -> cameraDevice.takeStillPhoto()
    }

    cameraDevice.takeStillPhoto()
}

private fun CameraDevice.startPreviewSafely() {
    try {
        startPreview()
    } catch (ignore: CameraException) {
    }
}
