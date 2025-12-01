package com.example.itplaneta.domain

import androidx.camera.core.ImageAnalysis
import com.google.zxing.NotFoundException

interface QrCodeAnalyzerFactory {
    fun create(
        onSuccess: (String) -> Unit,
        onFail: (NotFoundException) -> Unit
    ): ImageAnalysis.Analyzer
}
