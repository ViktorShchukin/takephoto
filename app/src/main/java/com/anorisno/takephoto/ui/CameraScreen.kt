package com.anorisno.takephoto.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anorisno.takephoto.tool.rotateBitmap
import java.util.concurrent.Executors

private const val TAG: String = "CameraScreen"

@Composable
fun CameraScreen(
) {
    val viewModel: CameraViewModel = remember { CameraViewModel() }

    val cameraState: CameraState by viewModel.uiState.collectAsStateWithLifecycle()
    CameraContent(
        imageConsumer = viewModel::updateAnalyzedImage,
        onImageSend = viewModel::sendCurrentImageToServer
    )
}

@Composable
fun CameraContent(
    imageConsumer: (image: Bitmap) -> Unit, //consumer for image analyzer
    onImageSend: () -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    Surface {
        Box {
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val executor = ContextCompat.getMainExecutor(context)

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val analyserExecutor = Executors.newSingleThreadExecutor()

                        val preview = Preview.Builder().build()
                            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                        val imageAnalyzer: ImageAnalysis = ImageAnalysis
                            .Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                            .build()
                            .also {
                                it.setAnalyzer(analyserExecutor) { image ->
                                    val bitmap: Bitmap = image.toBitmap().rotateBitmap(image.imageInfo.rotationDegrees)
                                    imageConsumer(bitmap)
                                    image.close()
                                }
                            }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
                        } catch (e: Exception) {
                            Log.e(TAG, "can't bind camera to lifecycle", e)
                        }
                    }, executor)
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
            Button(
                onClick = {onImageSend()}
            ) {
                Text("send to server")
            }
        }
    }
}