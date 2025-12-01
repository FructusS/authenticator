package com.example.itplaneta.data

import androidx.camera.core.ImageAnalysis
import com.example.itplaneta.core.camera.QrCodeAnalyzer
import com.example.itplaneta.domain.QrCodeAnalyzerFactory
import com.google.zxing.NotFoundException
import javax.inject.Inject

class QrCodeAnalyzerFactoryImpl @Inject constructor() : QrCodeAnalyzerFactory {
    override fun create(
        onSuccess: (String) -> Unit,
        onFail: (NotFoundException) -> Unit
    ): ImageAnalysis.Analyzer = QrCodeAnalyzer(onSuccess, onFail)
}
