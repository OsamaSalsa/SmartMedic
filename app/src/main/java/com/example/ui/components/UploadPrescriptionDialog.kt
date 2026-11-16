package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MedicationEntity
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.localizeInstruction
import com.example.ui.locale.localizeFrequency
import com.example.ui.locale.localizeDosage
import com.example.ui.locale.localizeTime
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.StatusNormal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.isAppInDarkTheme
import com.example.ui.viewmodel.MedicalViewModel
import com.example.util.LabReportFileHelper
import com.example.util.ProcessedReportFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * AI prescription upload and medication extraction dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPrescriptionDialog(
    viewModel: MedicalViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()

    val isExtracting by viewModel.isExtractingPrescription.collectAsStateWithLifecycle()
    val extractedMedications by viewModel.extractedPrescriptionMedications.collectAsStateWithLifecycle()
    val errorMessage by viewModel.prescriptionErrorMessage.collectAsStateWithLifecycle()

    var selectedFile by remember { mutableStateOf<ProcessedReportFile?>(null) }
    var additionalNotes by remember { mutableStateOf("") }
    var isReadingFile by remember { mutableStateOf(false) }

    // Dialog for editing an extracted medication
    var editingMedicationIndex by remember { mutableStateOf<Int?>(null) }
    var showAddManualMedDialog by remember { mutableStateOf(false) }

    // 1. Camera Launcher (Direct Snapshot)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            isReadingFile = true
            scope.launch {
                val processed = withContext(Dispatchers.IO) {
                    LabReportFileHelper.processBitmap(
                        bitmap,
                        "Prescription_${System.currentTimeMillis()}.jpg"
                    )
                }
                selectedFile = processed
                isReadingFile = false
                if (processed != null) {
                    val msg = if (strings.isArabic) "تم التقاط الصورة، جاري تحليل الوصفة بالذكاء الاصطناعي..." else if (strings.isRussian) "Фото получено, анализ рецепта с помощью ИИ..." else "Photo captured, analyzing prescription with AI..."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.processUploadedPrescription(
                        fileBase64 = processed.base64Data,
                        mimeType = processed.mimeType,
                        pageImagesBase64 = processed.pageImagesBase64,
                        notes = additionalNotes
                    ) { count ->
                        if (count > 0) {
                            val succMsg = if (strings.isArabic) "تم استخراج $count أدوية بنجاح!" else if (strings.isRussian) "Успешно извлечено $count лекарств!" else "Extracted $count medications successfully!"
                            Toast.makeText(context, succMsg, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errMsg = if (strings.isArabic) "تعذر تجهيز الصورة الملتقطة" else if (strings.isRussian) "Не удалось обработать снимок" else "Could not process captured image"
                    Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 2. Gallery / Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isReadingFile = true
            scope.launch {
                val processed = withContext(Dispatchers.IO) {
                    LabReportFileHelper.processUri(context, uri)
                }
                selectedFile = processed
                isReadingFile = false
                if (processed != null) {
                    val msg = if (strings.isArabic) "تم اختيار الصورة، جاري استخراج الأدوية بالذكاء الاصطناعي..." else if (strings.isRussian) "Изображение выбрано, извлечение лекарств..." else "Image selected, extracting medications with AI..."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.processUploadedPrescription(
                        fileBase64 = processed.base64Data,
                        mimeType = processed.mimeType,
                        pageImagesBase64 = processed.pageImagesBase64,
                        notes = additionalNotes
                    ) { count ->
                        if (count > 0) {
                            val succMsg = if (strings.isArabic) "تم استخراج $count أدوية بنجاح!" else if (strings.isRussian) "Успешно извлечено $count лекарств!" else "Extracted $count medications successfully!"
                            Toast.makeText(context, succMsg, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errMsg = if (strings.isArabic) "تعذر قراءة ملف الصورة المحدد" else if (strings.isRussian) "Не удалось прочитать выбранное изображение" else "Could not read selected image file"
                    Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 3. PDF Picker Launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isReadingFile = true
            scope.launch {
                val processed = withContext(Dispatchers.IO) {
                    LabReportFileHelper.processUri(context, uri)
                }
                selectedFile = processed
                isReadingFile = false
                if (processed != null) {
                    val msg = if (strings.isArabic) "تم تحميل ملف PDF، جاري استخراج الأدوية بالذكاء الاصطناعي..." else if (strings.isRussian) "PDF загружен, извлечение лекарств..." else "PDF loaded, extracting medications with AI..."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.processUploadedPrescription(
                        fileBase64 = processed.base64Data,
                        mimeType = processed.mimeType,
                        pageImagesBase64 = processed.pageImagesBase64,
                        notes = additionalNotes
                    ) { count ->
                        if (count > 0) {
                            val succMsg = if (strings.isArabic) "تم استخراج $count أدوية بنجاح!" else if (strings.isRussian) "Успешно извлечено $count лекарств!" else "Extracted $count medications successfully!"
                            Toast.makeText(context, succMsg, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errMsg = if (strings.isArabic) "تعذر قراءة ملف PDF" else if (strings.isRussian) "Не удалось прочитать PDF" else "Could not read PDF file"
                    Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Dialog(
        onDismissRequest = {
            if (!isExtracting) {
                viewModel.clearExtractedPrescription()
                onDismiss()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = strings.scanPrescriptionHeader,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.scanPrescriptionSubheader,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            viewModel.clearExtractedPrescription()
                            onDismiss()
                        },
                        enabled = !isExtracting
                    ) {
                        Icon(Icons.Default.Close, contentDescription = strings.close)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Method Selection Cards (Only when no file is chosen or during reselection)
                    Text(
                        text = strings.choosePrescriptionMethod,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Camera Button Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !isExtracting) {
                                    cameraLauncher.launch(null)
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedFile?.isPdf == false) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedFile?.isPdf == false) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(strings.cameraCaptureTitle, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                Text(strings.cameraCaptureSubtitle, fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                            }
                        }

                        // Gallery / Image Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !isExtracting) {
                                    photoPickerLauncher.launch("image/*")
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = Color(0xFF1976D2),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(strings.galleryImageTitle, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                Text(strings.galleryImageSubtitle, fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                            }
                        }

                        // PDF Card
                        val isDarkPresc = isAppInDarkTheme()
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !isExtracting) {
                                    pdfPickerLauncher.launch("application/pdf")
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedFile?.isPdf == true) {
                                    if (isDarkPresc) MaterialTheme.colorScheme.primaryContainer else Color(0xFFFCE4EC)
                                } else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedFile?.isPdf == true) Color(0xFFD81B60) else Color.Transparent
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = Color(0xFFD81B60),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(strings.pdfPrescriptionTitle, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                Text(strings.pdfPrescriptionSubtitle, fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                            }
                        }
                    }

                    // Loading file indicator
                    if (isReadingFile) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                                Text(strings.analyzingPrescriptionText, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    // Selected File Preview Card
                    if (selectedFile != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = StatusNormal,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Column {
                                            Text(
                                                text = selectedFile?.fileName ?: strings.prescriptionDocument,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (selectedFile?.isPdf == true) strings.pdfReadyForAnalysis else strings.photoReadyForAnalysis,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { selectedFile = null },
                                        enabled = !isExtracting
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = strings.deletePhoto, tint = Color.Red, modifier = Modifier.size(18.dp))
                                    }
                                }

                                // Image thumbnail if present
                                val previewBmp = selectedFile?.previewBitmap
                                if (previewBmp != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            bitmap = previewBmp.asImageBitmap(),
                                            contentDescription = strings.previewPrescription,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Additional Notes Field
                    OutlinedTextField(
                        value = additionalNotes,
                        onValueChange = { additionalNotes = it },
                        label = { Text(strings.prescriptionNotesPlaceholder) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Error message alert
                    if (!errorMessage.isNullOrBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                Text(errorMessage ?: "", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    }

                    // Action Button: Start Extraction
                    if (selectedFile != null || additionalNotes.isNotBlank()) {
                        Button(
                            onClick = {
                                viewModel.processUploadedPrescription(
                                    fileBase64 = selectedFile?.base64Data,
                                    mimeType = selectedFile?.mimeType ?: "image/jpeg",
                                    pageImagesBase64 = selectedFile?.pageImagesBase64 ?: emptyList(),
                                    notes = additionalNotes
                                ) { count ->
                                    if (count > 0) {
                                        val succMsg = if (strings.isArabic) "تم استخراج $count أدوية بنجاح!" else if (strings.isRussian) "Успешно извлечено $count лекарств!" else "Extracted $count medications successfully!"
                                        Toast.makeText(context, succMsg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            enabled = !isExtracting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_extract_prescription"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalRed)
                        ) {
                            if (isExtracting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(strings.analyzingPrescriptionText, fontSize = 13.sp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.analyzePrescriptionBtn, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Section 2: Extracted Medications List
                    if (extractedMedications.isNotEmpty()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${strings.extractedMedicationsTitle} (${extractedMedications.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            TextButton(
                                onClick = { showAddManualMedDialog = true }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.addAnotherMedication, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        // List of Extracted Medications Cards
                        extractedMedications.forEachIndexed { index, med ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Medication,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = med.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "${strings.dosageLabel}: ${strings.localizeDosage(med.dosage)}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(
                                                onClick = { editingMedicationIndex = index },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = strings.edit, tint = Color(0xFF1976D2), modifier = Modifier.size(18.dp))
                                            }
                                            IconButton(
                                                onClick = { viewModel.removeExtractedMedication(index) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Details Badges Row
                                    val isDarkCard = isAppInDarkTheme()
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        // Instructions Badge
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isDarkCard) Color(0xFF143823) else Color(0xFFE8F5E9)
                                        ) {
                                            Text(
                                                text = strings.localizeInstruction(med.instructions),
                                                fontSize = 10.5.sp,
                                                color = if (isDarkCard) Color(0xFF4ADE80) else Color(0xFF2E7D32),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // Frequency Badge
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isDarkCard) Color(0xFF1E293B) else Color(0xFFE3F2FD)
                                        ) {
                                            Text(
                                                text = strings.localizeFrequency(med.frequency),
                                                fontSize = 10.5.sp,
                                                color = if (isDarkCard) Color(0xFF60A5FA) else Color(0xFF1565C0),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // Time Badge
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isDarkCard) Color(0xFF3E2D07) else Color(0xFFFFF3E0)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = if (isDarkCard) Color(0xFFFBBF24) else Color(0xFFE65100), modifier = Modifier.size(12.dp))
                                                Text(
                                                    text = strings.localizeTime(med.time),
                                                    fontSize = 10.5.sp,
                                                    color = if (isDarkCard) Color(0xFFFBBF24) else Color(0xFFE65100),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    val currentLang = com.example.ui.locale.LocalAppLanguage.current
                                    val ruleFallback = remember(med.name, med.dosage, currentLang) {
                                        com.example.data.remote.GeminiService().generateRuleBasedMedicationDetails(med.name, med.dosage, currentLang)
                                    }
                                    val displayActive = med.activeIngredients.ifBlank { ruleFallback.activeIngredients }
                                    val displayUses = med.uses.ifBlank { ruleFallback.uses }
                                    val displaySide = med.sideEffects.ifBlank { ruleFallback.sideEffects }
                                    val displayFood = med.foodInteractions.ifBlank { ruleFallback.foodInteractions }

                                    // AI Details Section (Active Ingredients, Uses, Side Effects, Food Interactions)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    var isAiExpanded by remember { mutableStateOf(false) }
                                    val isDark = isAppInDarkTheme()

                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { isAiExpanded = !isAiExpanded },
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDark) 0.25f else 0.40f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.AutoAwesome,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                    Text(
                                                        text = strings.pharmaAnalysisHeader,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                                Icon(
                                                    imageVector = if (isAiExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            // Quick preview when collapsed
                                            if (!isAiExpanded) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "🔬 ${strings.activeIngredientsLabel}: $displayActive",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 1
                                                )
                                            }

                                            if (isAiExpanded) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                // 1. Active Ingredients
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (isDark) Color(0xFF221735) else Color(0xFFF5F3FF),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF7C3AED).copy(alpha = 0.45f) else Color(0xFFDDD6FE)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Top) {
                                                        Icon(Icons.Default.Science, contentDescription = null, tint = if (isDark) Color(0xFFA78BFA) else Color(0xFF7C3AED), modifier = Modifier.size(15.dp))
                                                        Column {
                                                            Text("${strings.activeIngredientsLabel}:", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFA78BFA) else Color(0xFF6D28D9))
                                                            Spacer(modifier = Modifier.height(2.dp))
                                                            Text(displayActive, fontSize = 10.5.sp, color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B), lineHeight = 16.sp)
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))

                                                // 2. Side Effects
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (isDark) Color(0xFF341517) else Color(0xFFFEF2F2),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFFEF4444).copy(alpha = 0.45f) else Color(0xFFFECACA)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Top) {
                                                        Icon(Icons.Default.Warning, contentDescription = null, tint = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626), modifier = Modifier.size(15.dp))
                                                        Column {
                                                            Text("${strings.sideEffectsLabel}:", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFF87171) else Color(0xFFB91C1C))
                                                            Spacer(modifier = Modifier.height(2.dp))
                                                            Text(displaySide, fontSize = 10.5.sp, color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF450A0A), lineHeight = 16.sp)
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))

                                                // 3. Uses
                                                if (displayUses.isNotBlank()) {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isDark) Color(0xFF0F291E) else Color(0xFFF0FDF4),
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF22C55E).copy(alpha = 0.45f) else Color(0xFFBBF7D0)),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Top) {
                                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (isDark) Color(0xFF4ADE80) else Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                                                            Column {
                                                                Text("${strings.usesLabel}:", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D))
                                                                Spacer(modifier = Modifier.height(2.dp))
                                                                Text(displayUses, fontSize = 10.5.sp, color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF14532D), lineHeight = 16.sp)
                                                            }
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                }

                                                // 4. Food Interactions
                                                if (displayFood.isNotBlank()) {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isDark) Color(0xFF312108) else Color(0xFFFFFBEB),
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFFF59E0B).copy(alpha = 0.45f) else Color(0xFFFDE68A)),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(8.dp),
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                            verticalAlignment = Alignment.Top
                                                        ) {
                                                            Icon(Icons.Default.Warning, contentDescription = null, tint = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                                            Column {
                                                                Text("${strings.foodInteractionsLabel}:", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309))
                                                                Spacer(modifier = Modifier.height(2.dp))
                                                                Text(displayFood, fontSize = 10.5.sp, color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF78350F), lineHeight = 16.sp)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (med.notes.isNotBlank() && med.notes != strings.autoExtractedAiNotes && !med.notes.contains("مستخرج") && !med.notes.contains("Auto-extracted") && !med.notes.contains("Извлечено")) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${strings.notes}: ${med.notes}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Save Action
                if (extractedMedications.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    Button(
                        onClick = {
                            viewModel.saveExtractedMedications {
                                val succMsg = if (strings.isArabic) "تم حفظ ${extractedMedications.size} أدوية وتفعيل تنبيهات الجرعات بنجاح!" else if (strings.isRussian) "Успешно сохранено ${extractedMedications.size} лекарств и включены напоминания!" else "Saved ${extractedMedications.size} medications and enabled reminders!"
                                Toast.makeText(
                                    context,
                                    succMsg,
                                    Toast.LENGTH_LONG
                                ).show()
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_save_prescription_meds"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${strings.saveMedicationsAndReminders} (${extractedMedications.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Sub-dialog: Edit Extracted Medication
    editingMedicationIndex?.let { idx ->
        if (idx in extractedMedications.indices) {
            val medToEdit = extractedMedications[idx]
            EditPrescriptionMedicationDialog(
                initialMedication = medToEdit,
                onDismiss = { editingMedicationIndex = null },
                onSave = { updated ->
                    viewModel.updateExtractedMedication(idx, updated)
                    editingMedicationIndex = null
                }
            )
        }
    }

    // Sub-dialog: Add Manual Medication to Extracted List
    if (showAddManualMedDialog) {
        EditPrescriptionMedicationDialog(
            initialMedication = MedicationEntity(
                name = "",
                dosage = "1 قرص",
                instructions = "بعد الأكل",
                time = "08:00 ص",
                hour = 8,
                minute = 0,
                frequency = "يومياً"
            ),
            isNew = true,
            onDismiss = { showAddManualMedDialog = false },
            onSave = { newMed ->
                viewModel.addExtractedMedication(newMed)
                showAddManualMedDialog = false
            }
        )
    }
}

/**
 * نافذة حوار سريعة لتعديل بيانات الدواء المستخرج أو إضافة دواء جديد للقائمة
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPrescriptionMedicationDialog(
    initialMedication: MedicationEntity,
    isNew: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (MedicationEntity) -> Unit
) {
    val strings = com.example.ui.locale.LocalAppStrings.current
    var name by remember { mutableStateOf(initialMedication.name) }
    var dosage by remember { mutableStateOf(initialMedication.dosage) }
    var instructions by remember { mutableStateOf(initialMedication.instructions) }
    var frequency by remember { mutableStateOf(initialMedication.frequency) }
    var hour by remember { mutableIntStateOf(initialMedication.hour) }
    var minute by remember { mutableIntStateOf(initialMedication.minute) }
    var notes by remember { mutableStateOf(initialMedication.notes) }
    var activeIngredients by remember { mutableStateOf(initialMedication.activeIngredients) }
    var uses by remember { mutableStateOf(initialMedication.uses) }
    var sideEffects by remember { mutableStateOf(initialMedication.sideEffects) }
    var foodInteractions by remember { mutableStateOf(initialMedication.foodInteractions) }
    var importantInfo by remember { mutableStateOf(initialMedication.importantInfo) }

    val instructionOptions = listOf(strings.afterFood, strings.beforeFood, strings.withFood, strings.beforeSleep)
    val frequencyOptions = listOf(strings.dailyFreq, strings.twiceDailyFreq, strings.thriceDailyFreq, strings.weeklyFreq, strings.asNeededFreq)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isNew) strings.addMedToPrescriptionTitle else strings.editMedInPrescriptionTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.medNamePlaceholder) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text(strings.medDosagePlaceholder) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(strings.usageInstructionsLabel, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    instructionOptions.take(3).forEach { opt ->
                        Surface(
                            modifier = Modifier.clickable { instructions = opt },
                            shape = RoundedCornerShape(8.dp),
                            color = if (instructions == opt) Color(0xFFEDE7F6) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (instructions == opt) Color(0xFF6A1B9A) else Color.Transparent
                            )
                        ) {
                            Text(
                                text = opt,
                                fontSize = 10.5.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                color = if (instructions == opt) Color(0xFF4A148C) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    instructionOptions.drop(3).forEach { opt ->
                        Surface(
                            modifier = Modifier.clickable { instructions = opt },
                            shape = RoundedCornerShape(8.dp),
                            color = if (instructions == opt) Color(0xFFEDE7F6) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (instructions == opt) Color(0xFF6A1B9A) else Color.Transparent
                            )
                        ) {
                            Text(
                                text = opt,
                                fontSize = 10.5.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                color = if (instructions == opt) Color(0xFF4A148C) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Text(strings.dosageFrequencyLabel, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    frequencyOptions.take(3).forEach { opt ->
                        Surface(
                            modifier = Modifier.clickable { frequency = opt },
                            shape = RoundedCornerShape(8.dp),
                            color = if (frequency == opt) Color(0xFFE3F2FD) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (frequency == opt) Color(0xFF1976D2) else Color.Transparent
                            )
                        ) {
                            Text(
                                text = opt,
                                fontSize = 10.5.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                color = if (frequency == opt) Color(0xFF0D47A1) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Time Pickers
                Text(strings.dailyReminderTimeLabel, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var hourStr by remember { mutableStateOf(hour.toString()) }
                    var minStr by remember { mutableStateOf(if (minute < 10) "0$minute" else "$minute") }

                    OutlinedTextField(
                        value = hourStr,
                        onValueChange = {
                            hourStr = it
                            val parsed = it.toIntOrNull()
                            if (parsed != null && parsed in 0..23) hour = parsed
                        },
                        label = { Text(strings.hourLabel) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = minStr,
                        onValueChange = {
                            minStr = it
                            val parsed = it.toIntOrNull()
                            if (parsed != null && parsed in 0..59) minute = parsed
                        },
                        label = { Text(strings.minuteLabel) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(strings.notes) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // AI Details Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Text(strings.aiDataAndInteractionsLabel, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        OutlinedTextField(
                            value = activeIngredients,
                            onValueChange = { activeIngredients = it },
                            label = { Text(strings.activeIngredientsLabel) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                        )

                        OutlinedTextField(
                            value = uses,
                            onValueChange = { uses = it },
                            label = { Text(strings.usesLabel) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                            maxLines = 2
                        )

                        OutlinedTextField(
                            value = sideEffects,
                            onValueChange = { sideEffects = it },
                            label = { Text(strings.sideEffectsLabel) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                            maxLines = 2
                        )

                        OutlinedTextField(
                            value = foodInteractions,
                            onValueChange = { foodInteractions = it },
                            label = { Text(strings.foodInteractionsLabel) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                            maxLines = 2
                        )

                        OutlinedTextField(
                            value = importantInfo,
                            onValueChange = { importantInfo = it },
                            label = { Text(strings.importantInfoLabel) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                            maxLines = 2
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val formattedTime = if (strings.isArabic) {
                            if (hour < 12) {
                                String.format(Locale.getDefault(), "%02d:%02d ص", if (hour == 0) 12 else hour, minute)
                            } else {
                                val h12 = if (hour == 12) 12 else hour - 12
                                String.format(Locale.getDefault(), "%02d:%02d م", h12, minute)
                            }
                        } else if (strings.isRussian) {
                            String.format(Locale.US, "%02d:%02d", hour, minute)
                        } else {
                            val isPm = hour >= 12
                            val h12 = when { hour == 0 -> 12; hour > 12 -> hour - 12; else -> hour }
                            String.format(Locale.US, "%02d:%02d %s", h12, minute, if (isPm) "PM" else "AM")
                        }
                        onSave(
                            initialMedication.copy(
                                name = name.trim(),
                                dosage = dosage.trim(),
                                instructions = instructions,
                                frequency = frequency,
                                hour = hour,
                                minute = minute,
                                time = formattedTime,
                                notes = notes,
                                activeIngredients = activeIngredients,
                                uses = uses,
                                sideEffects = sideEffects,
                                foodInteractions = foodInteractions,
                                importantInfo = importantInfo
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalRed)
            ) {
                Text(strings.saveChanges)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
