package com.example.itplaneta.camera

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

class QrCodeAnalyzer(private val onQrCodeScanned : (String) -> Unit): ImageAnalysis.Analyzer {
    private val imageFormats = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888,
    )
    override fun analyze(image: ImageProxy) {
        if (image.format in imageFormats){
            val bytes = image.planes.first().buffer.toByteArray()

            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
            0,
            0,
                image.width,
                image.height,
                false
            )
            val bitmap = BinaryBitmap(HybridBinarizer(source))


            try {
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                BarcodeFormat.QR_CODE,
                                BarcodeFormat.AZTEC
                            )
                        )
                    )
                }.decode(bitmap)
                onQrCodeScanned(result.text)
            } catch (e: NotFoundException) {
                e.printStackTrace()
            } finally {
                image.close()
            }

        }


    }

    private fun ByteBuffer.toByteArray() : ByteArray{
        rewind()
        return ByteArray(remaining()).also {
            get(it)
        }
    }
}