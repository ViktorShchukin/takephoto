package com.anorisno.takephoto.ui

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.ViewModel
import com.anorisno.takephoto.httpclient.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.nio.file.Files
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

class CameraViewModel : ViewModel() {

    private val _uiSate = MutableStateFlow(CameraState())
    val uiState: StateFlow<CameraState> = _uiSate.asStateFlow()

    private val httpClient = HttpClient()




    fun updateAnalyzedImage(image: Bitmap) {
        _uiSate.update { currentState ->
            currentState.copy(lastImage = image)

        }
    }

    fun sendImageToServer(image: Bitmap) {
        val timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        val file: File = kotlin.io.path.createTempFile("photo", ".png").toFile()
        image.compress(Bitmap.CompressFormat.PNG, 100, file.outputStream())
        httpClient.sendFile(file)
    }

    fun sendCurrentImageToServer() {
            sendImageToServer(uiState.value.lastImage!!)
    }
}