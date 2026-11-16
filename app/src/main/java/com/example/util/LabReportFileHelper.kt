package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64

data class ProcessedReportFile(
    val fileName: String,
    val mimeType: String,
    val base64Data: String,
    val isPdf: Boolean,
    val previewBitmap: Bitmap? = null,
    val pageImagesBase64: List<String> = emptyList(),
    val fileSizeBytes: Long = 0L
)

object LabReportFileHelper {

    fun processUri(context: Context, uri: Uri): ProcessedReportFile? {
        return try {
            val contentResolver = context.contentResolver
            val detectedMime = contentResolver.getType(uri) ?: ""
            val isPdf = detectedMime.contains("pdf", ignoreCase = true) || uri.toString().endsWith(".pdf", ignoreCase = true)

            var fileName = if (isPdf) "Medical_Lab_Report.pdf" else "Lab_Test_Image.jpg"
            var fileSizeBytes = 0L

            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) {
                        val name = cursor.getString(nameIndex)
                        if (!name.isNullOrBlank()) fileName = name
                    }
                    if (sizeIndex != -1) {
                        fileSizeBytes = cursor.getLong(sizeIndex)
                    }
                }
            }

            val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            val mimeType = if (isPdf) "application/pdf" else if (detectedMime.startsWith("image/")) detectedMime else "image/jpeg"

            val pageImages = mutableListOf<String>()
            var previewBitmap: Bitmap? = null

            if (isPdf) {
                try {
                    val pfd = contentResolver.openFileDescriptor(uri, "r")
                    if (pfd != null) {
                        val renderer = PdfRenderer(pfd)
                        val totalPages = renderer.pageCount
                        for (i in 0 until totalPages) {
                            val page = renderer.openPage(i)
                            // Render page at high resolution (1400px wide) so all numbers and text are crystal clear
                            val scale = (1400f / page.width.toFloat()).coerceIn(1.2f, 2.5f)
                            val width = (page.width * scale).toInt()
                            val height = (page.height * scale).toInt()
                            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                            bitmap.eraseColor(android.graphics.Color.WHITE)
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            page.close()

                            if (i == 0) {
                                previewBitmap = bitmap
                            }

                            val stream = java.io.ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 88, stream)
                            val imgB64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                            pageImages.add(imgB64)
                        }
                        renderer.close()
                        pfd.close()
                    }
                } catch (_: Exception) {
                    // Fallback
                }
            } else {
                try {
                    contentResolver.openInputStream(uri)?.use { stream ->
                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        BitmapFactory.decodeStream(stream, null, options)

                        var sampleSize = 1
                        while (options.outWidth / sampleSize > 1400 || options.outHeight / sampleSize > 1400) {
                            sampleSize *= 2
                        }

                        contentResolver.openInputStream(uri)?.use { stream2 ->
                            val decodeOpts = BitmapFactory.Options().apply {
                                inSampleSize = sampleSize
                            }
                            previewBitmap = BitmapFactory.decodeStream(stream2, null, decodeOpts)
                        }
                    }
                    if (previewBitmap != null) {
                        val stream = java.io.ByteArrayOutputStream()
                        previewBitmap?.compress(Bitmap.CompressFormat.JPEG, 88, stream)
                        pageImages.add(Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP))
                    } else {
                        pageImages.add(base64)
                    }
                } catch (_: Exception) {
                    pageImages.add(base64)
                }
            }

            ProcessedReportFile(
                fileName = fileName,
                mimeType = mimeType,
                base64Data = base64,
                isPdf = isPdf,
                previewBitmap = previewBitmap,
                pageImagesBase64 = pageImages,
                fileSizeBytes = if (fileSizeBytes > 0) fileSizeBytes else bytes.size.toLong()
            )
        } catch (e: Exception) {
            null
        }
    }

    fun processBitmap(bitmap: Bitmap, fileName: String = "Prescription_Capture.jpg"): ProcessedReportFile? {
        return try {
            val maxDim = 1600
            val scaledBitmap = if (bitmap.width > maxDim || bitmap.height > maxDim) {
                val ratio = (maxDim.toFloat() / maxOf(bitmap.width, bitmap.height).toFloat())
                Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * ratio).toInt(),
                    (bitmap.height * ratio).toInt(),
                    true
                )
            } else {
                bitmap
            }

            val stream = java.io.ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 88, stream)
            val bytes = stream.toByteArray()
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)

            ProcessedReportFile(
                fileName = fileName,
                mimeType = "image/jpeg",
                base64Data = base64,
                isPdf = false,
                previewBitmap = scaledBitmap,
                pageImagesBase64 = listOf(base64),
                fileSizeBytes = bytes.size.toLong()
            )
        } catch (e: Exception) {
            null
        }
    }
}
