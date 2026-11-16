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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LabTestEntity
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.MedicalRedDark
import com.example.ui.theme.MedicalRedSoft
import com.example.ui.theme.PrimaryRedGradient
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenSoft
import com.example.ui.theme.isAppInDarkTheme
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.localizeCategory
import com.example.ui.locale.localizeTestName
import com.example.ui.locale.localizeStatus
import com.example.ui.viewmodel.MedicalViewModel
import com.example.util.LabReportFileHelper
import com.example.util.ProcessedReportFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * نافذة حوار لرفع تقارير الفحوصات بصيغة PDF أو صورة
 * واستخراج قيم وأسماء الفحوصات بواسطة الذكاء الاصطناعي وتخزينها
 */
@Composable
fun UploadLabReportDialog(
    viewModel: MedicalViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val strings = com.example.ui.locale.LocalAppStrings.current
    val scope = rememberCoroutineScope()

    val isExtracting by viewModel.isExtractingReport.collectAsStateWithLifecycle()
    val extractedTests by viewModel.extractedReportTests.collectAsStateWithLifecycle()
    val reportErrorMessage by viewModel.reportExtractionErrorMessage.collectAsStateWithLifecycle()

    var selectedFile by remember { mutableStateOf<ProcessedReportFile?>(null) }
    var additionalNotes by remember { mutableStateOf("") }
    var isReadingFile by remember { mutableStateOf(false) }

    // Launcher for PDF files
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isReadingFile = true
            scope.launch {
                val processed = withContext(Dispatchers.IO) {
                    LabReportFileHelper.processUri(context, uri)
                }
                isReadingFile = false
                if (processed != null) {
                    selectedFile = processed
                    val msg = if (strings.isArabic) "تم تحميل ملف PDF، جاري قراءة واستخراج الفحوصات بالذكاء الاصطناعي..."
                              else if (strings.isRussian) "PDF загружен, считывание данных с помощью ИИ..."
                              else "PDF loaded, extracting lab tests with AI..."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.processUploadedReport(
                        fileBase64 = processed.base64Data,
                        mimeType = processed.mimeType,
                        pageImagesBase64 = processed.pageImagesBase64,
                        reportNotes = additionalNotes.ifBlank { null }
                    ) { count ->
                        if (count > 0) {
                            val succMsg = if (strings.isArabic) "تم استخراج وحفظ $count فحصاً مخبرياً بنجاح في سجلك الطبي!"
                                          else if (strings.isRussian) "Успешно извлечено и сохранено $count анализов!"
                                          else "Successfully extracted & saved $count lab tests!"
                            Toast.makeText(context, succMsg, Toast.LENGTH_LONG).show()
                        } else {
                            val failMsg = if (strings.isArabic) "لم يتم العثور على فحوصات واضحة في ملف PDF"
                                          else if (strings.isRussian) "Анализы не найдены в PDF"
                                          else "No clear lab tests found in PDF"
                            Toast.makeText(context, failMsg, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    val errReadMsg = if (strings.isArabic) "تعذر قراءة ملف PDF، يرجى التأكد من صلاحية الملف"
                                     else if (strings.isRussian) "Не удалось прочитать PDF файл"
                                     else "Could not read PDF file, please check file"
                    Toast.makeText(context, errReadMsg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Launcher for Images (JPG, PNG, WebP)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isReadingFile = true
            scope.launch {
                val processed = withContext(Dispatchers.IO) {
                    LabReportFileHelper.processUri(context, uri)
                }
                isReadingFile = false
                if (processed != null) {
                    selectedFile = processed
                    val msg = if (strings.isArabic) "تم اختيار الصورة، جاري استخراج الفحوصات بالذكاء الاصطناعي..."
                              else if (strings.isRussian) "Изображение выбрано, извлечение данных ИИ..."
                              else "Photo selected, extracting lab tests with AI..."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.processUploadedReport(
                        fileBase64 = processed.base64Data,
                        mimeType = processed.mimeType,
                        pageImagesBase64 = processed.pageImagesBase64,
                        reportNotes = additionalNotes.ifBlank { null }
                    ) { count ->
                        if (count > 0) {
                            val succMsg = if (strings.isArabic) "تم استخراج وحفظ $count فحصاً مخبرياً بنجاح في سجلك الطبي!"
                                          else if (strings.isRussian) "Успешно извлечено и сохранено $count анализов!"
                                          else "Successfully extracted & saved $count lab tests!"
                            Toast.makeText(context, succMsg, Toast.LENGTH_LONG).show()
                        } else {
                            val failMsg = if (strings.isArabic) "لم يتم العثور على فحوصات واضحة في الصورة"
                                          else if (strings.isRussian) "Анализы не найдены на фото"
                                          else "No clear lab tests found in photo"
                            Toast.makeText(context, failMsg, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    val errReadMsg = if (strings.isArabic) "تعذر قراءة صورة الفحص، يرجى المحاولة مرة أخرى"
                                     else if (strings.isRussian) "Не удалось прочитать фото анализа"
                                     else "Could not read photo file"
                    Toast.makeText(context, errReadMsg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!isExtracting && !isReadingFile) {
                viewModel.dismissExtractedTests()
                onDismiss()
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = MedicalRedSoft,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MedicalRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = strings.uploadReportHeader,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = strings.uploadReportSubheader,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isReadingFile || isExtracting) {
                    // Loading State
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MedicalRed,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = if (isReadingFile) {
                                    if (strings.isArabic) "جاري قراءة وتجهيز الملف..."
                                    else if (strings.isRussian) "Чтение и обработка файла..."
                                    else "Reading & preparing file..."
                                } else {
                                    if (strings.isArabic) "جاري قراءة تقرير الفحص واستخراج القيم بالذكاء الاصطناعي..."
                                    else if (strings.isRussian) "ИИ распознает результаты анализа и значения..."
                                    else "Reading report & extracting test values with AI..."
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (strings.isArabic) "يتم التعرّف على أسماء الفحوصات، القيم الرقمية، الوحدات، والمعدلات الطبيعية بدقة."
                                       else if (strings.isRussian) "Точное распознавание названий анализов, значений, единиц и норм."
                                       else "Accurate recognition of test names, numeric values, units, and normal ranges.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else if (extractedTests.isNotEmpty()) {
                    val isDarkReview = isAppInDarkTheme()
                    // Review Extracted Tests Phase
                    Surface(
                        color = if (isDarkReview) Color(0xFF143823) else SuccessGreenSoft,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isDarkReview) Color(0xFF4ADE80) else SuccessGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (strings.isArabic) "تم استخراج وحفظ ${extractedTests.size} فحصاً بنجاح في سجلك الطبي!"
                                           else if (strings.isRussian) "Успешно извлечено и сохранено ${extractedTests.size} анализов!"
                                           else "Successfully extracted & saved ${extractedTests.size} tests to your record!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isDarkReview) Color(0xFFA7F3D0) else Color(0xFF065F46)
                                )
                                Text(
                                    text = if (strings.isArabic) "أُضيفت الفحوصات مباشرة لسجلك. يمكنك مراجعة أو تعديل أو حذف أي قيمة هنا."
                                           else if (strings.isRussian) "Анализы добавлены в карту. Вы можете проверить, изменить или удалить значения."
                                           else "Tests added directly. You can review, edit, or delete any value here.",
                                    fontSize = 11.sp,
                                    color = if (isDarkReview) Color(0xFF6EE7B7) else Color(0xFF047857)
                                )
                            }
                        }
                    }

                    // File summary & Preview if available
                    selectedFile?.let { file ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (file.previewBitmap != null) {
                                    Image(
                                        bitmap = file.previewBitmap.asImageBitmap(),
                                        contentDescription = strings.previewReport,
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Surface(
                                        color = if (file.isPdf) MaterialTheme.colorScheme.primaryContainer else if (isAppInDarkTheme()) Color(0xFF0C3B5E) else Color(0xFFE0F2FE),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.size(52.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (file.isPdf) Icons.Default.PictureAsPdf else Icons.Default.Description,
                                                contentDescription = null,
                                                tint = if (file.isPdf) MaterialTheme.colorScheme.primary else Color(0xFF0284C7),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = file.fileName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (file.isPdf) strings.pdfDocumentLabel else strings.imageDocumentLabel,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = strings.extractedTestsTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Extracted Items List
                    extractedTests.forEachIndexed { index, test ->
                        ExtractedItemEditCard(
                            test = test,
                            onUpdate = { updated -> viewModel.updateExtractedTest(index, updated) },
                            onDelete = { viewModel.removeExtractedTest(index) }
                        )
                    }

                } else {
                    val isDark = isAppInDarkTheme()

                    // Upload Choices
                    Text(
                        text = strings.uploadReportChooseMethod,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // PDF Button Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { pdfPickerLauncher.launch("application/pdf") }
                            .testTag("upload_pdf_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) else Color(0xFFFFF1F2)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, MedicalRed.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                color = MedicalRed,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.PictureAsPdf,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.pdfUploadTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDark) MaterialTheme.colorScheme.primary else MedicalRedDark
                                )
                                Text(
                                    text = strings.pdfUploadSubtitle,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Image Button Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { imagePickerLauncher.launch("image/*") }
                            .testTag("upload_image_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) else Color(0xFFF0FDF4)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, SuccessGreen.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                color = SuccessGreen,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.imageUploadTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDark) SuccessGreen else Color(0xFF166534)
                                )
                                Text(
                                    text = strings.imageUploadSubtitle,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Additional Notes Field
                    OutlinedTextField(
                        value = additionalNotes,
                        onValueChange = { additionalNotes = it },
                        label = { Text(strings.reportNotesLabel) },
                        placeholder = { Text(strings.reportNotesPlaceholder) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Error Message Banner if any
                    if (!reportErrorMessage.isNullOrBlank()) {
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
                                Text(reportErrorMessage ?: "", fontSize = 12.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (extractedTests.isNotEmpty()) {
                Button(
                    onClick = {
                        viewModel.saveAllExtractedTests {
                            val savedMsg = if (strings.isArabic) "تم تثبيت وحفظ ${extractedTests.size} فحصاً بنجاح في سجلك الطبي!"
                                else if (strings.isRussian) "Успешно сохранено ${extractedTests.size} анализов!"
                                else "Successfully saved ${extractedTests.size} tests!"
                            Toast.makeText(
                                context,
                                savedMsg,
                                Toast.LENGTH_SHORT
                            ).show()
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalRed)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${strings.finishAndSave} (${extractedTests.size})")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.dismissExtractedTests()
                    onDismiss()
                }
            ) {
                Text(if (extractedTests.isNotEmpty()) strings.close else strings.cancel)
            }
        }
    )
}

/**
 * بطاقة عرض وتعديل الفحص المستخرج
 */
@Composable
fun ExtractedItemEditCard(
    test: LabTestEntity,
    onUpdate: (LabTestEntity) -> Unit,
    onDelete: () -> Unit
) {
    val strings = com.example.ui.locale.LocalAppStrings.current
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(test.testName) }
    var editValue by remember { mutableStateOf(test.value) }
    var editUnit by remember { mutableStateOf(test.unit) }
    var editRange by remember { mutableStateOf(test.referenceRange) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text(strings.testNameLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = editValue,
                        onValueChange = { editValue = it },
                        label = { Text(strings.resultLabel) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = editUnit,
                        onValueChange = { editUnit = it },
                        label = { Text(strings.unit) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = editRange,
                    onValueChange = { editRange = it },
                    label = { Text(strings.refRangeLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { isEditing = false }) { Text(strings.cancel) }
                    Button(
                        onClick = {
                            onUpdate(
                                test.copy(
                                    testName = editName,
                                    value = editValue,
                                    unit = editUnit,
                                    referenceRange = editRange
                                )
                            )
                            isEditing = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalRed)
                    ) {
                        Text(strings.saveEdit)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = strings.localizeTestName(test.testName),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            StatusBadge(status = test.statusFlag.ifBlank { "Normal" })
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${strings.resultLabel}: ${test.value} ${test.unit}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (test.referenceRange.isNotBlank()) {
                                Text(
                                    text = "• ${strings.normal}: ${test.referenceRange}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (test.testCategory.isNotBlank()) {
                            Text(
                                text = "${strings.categoryLabel}: ${strings.localizeCategory(test.testCategory)}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { isEditing = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = strings.edit, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
