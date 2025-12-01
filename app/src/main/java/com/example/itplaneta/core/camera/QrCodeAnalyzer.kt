package com.example.itplaneta.core.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.NotFoundException
import timber.log.Timber
import java.nio.ByteBuffer
import com.example.itplaneta.core.utils.Result
/**
 * Анализатор, который извлекает Y-плоскость из ImageProxy и передаёт её в ZxingDecoder.
 *
 * - onSuccess вызывается при успешном декодировании (com.google.zxing.Result).
 * - onFail вызывается, когда ZXing не нашёл код (NotFoundException).
 *
 * Важно: analyzer закрывает imageProxy (image.use { ... }), поэтому вызывающая сторона
 * не должна закрывать imageProxy повторно.
 */
class QrCodeAnalyzer(
    private val onSuccess: (String) -> Unit,
    private val onFail: (NotFoundException) -> Unit
) : ImageAnalysis.Analyzer {

    @Volatile
    private var handled = false

    override fun analyze(image: ImageProxy) {
        if (handled) {
            image.close()
            return
        }
        image.use { imageProxy ->
            try {
                val rotation = imageProxy.imageInfo.rotationDegrees
                val yPlane = imageProxy.planes.firstOrNull()
                if (yPlane == null) {
                    Timber.w("QrCodeAnalyzer: no planes found")
                    return
                }

                val data = yPlane.buffer.toByteArray()
                var width = imageProxy.width
                var height = imageProxy.height
                var processedData = data

                if (rotation == 90 || rotation == 270) {
                    processedData = rotateYPlane90or270(data, width, height, rotation)
                    val tmp = width
                    width = height
                    height = tmp
                }

                ZxingDecoder.decodeYuvLuminanceSource(
                    data = processedData,
                    dataWidth = width,
                    dataHeight = height,
                    onSuccess = { result ->
                        if (!handled) {
                            handled = true
                            try {
                                onSuccess(result.text)
                            } catch (e: Throwable) {
                                Timber.e(e, "onSuccess handler failed")
                            }
                        }
                    },
                    onError = { notFoundEx ->
                        try {
                            onFail(notFoundEx)
                        } catch (e: Throwable) {
                            Timber.e(e, "onFail handler failed")
                        }
                    }
                )
            } catch (nf: NotFoundException) {
                try {
                    onFail(nf)
                } catch (e: Throwable) {
                    Timber.e(e, "onFail handler failed")
                }
            } catch (e: Exception) {
                Timber.e(e, "QrCodeAnalyzer failed to analyze frame")
            }
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val bytes = ByteArray(remaining())
        get(bytes)
        return bytes
    }

    private fun rotateYPlane90or270(input: ByteArray, width: Int, height: Int, rotationDegrees: Int): ByteArray {
        if (rotationDegrees != 90 && rotationDegrees != 270) return input
        val output = ByteArray(input.size)

        if (rotationDegrees == 90) {
            // Rotate 90° clockwise
            // output[x * height + (height - y - 1)] = input[y * width + x]
            for (y in 0 until height) {
                val rowOffset = y * width
                for (x in 0 until width) {
                    val inIndex = rowOffset + x
                    val outIndex = x * height + (height - y - 1)
                    if (inIndex in input.indices && outIndex in output.indices) {
                        output[outIndex] = input[inIndex]
                    }
                }
            }
        } else {
            // rotationDegrees == 270 -> Rotate 270° clockwise (or 90° counter-clockwise)
            // output[(width - x - 1) * height + y] = input[y * width + x]
            for (y in 0 until height) {
                val rowOffset = y * width
                for (x in 0 until width) {
                    val inIndex = rowOffset + x
                    val outIndex = (width - x - 1) * height + y
                    if (inIndex in input.indices && outIndex in output.indices) {
                        output[outIndex] = input[inIndex]
                    }
                }
            }
        }

        return output
    }
}