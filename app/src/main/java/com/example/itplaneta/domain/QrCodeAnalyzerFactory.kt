package com.example.itplaneta.domain

import androidx.camera.core.ImageAnalysis
import com.example.itplaneta.core.camera.QrCodeAnalyzer
import com.google.zxing.NotFoundException

interface QrCodeAnalyzerFactory {
    fun create(
        onSuccess: (String) -> Unit,
        onFail: (NotFoundException) -> Unit
    ): QrCodeAnalyzer
}
