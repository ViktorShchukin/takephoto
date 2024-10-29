package com.anorisno.takephoto.ui

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val cameraPermission: PermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    MainContent(
        hasCameraPermission = cameraPermission.status.isGranted,
        onRequestPermission = cameraPermission::launchPermissionRequest
    )
}

@Composable
fun MainContent(
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    if (hasCameraPermission) {
        CameraScreen()
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}