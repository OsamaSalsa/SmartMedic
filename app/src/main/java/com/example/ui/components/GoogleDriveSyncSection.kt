package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.ui.theme.isAppInDarkTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DriveSyncFileEntity
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.MedicalRedSoft
import com.example.ui.viewmodel.MedicalViewModel
import com.example.util.GoogleDriveBackupManager
import com.example.util.PdfReportExporter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GoogleDriveSyncCard(
    viewModel: MedicalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = com.example.ui.locale.LocalAppStrings.current
    val driveFiles by viewModel.driveFiles.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isDriveSyncing.collectAsStateWithLifecycle()
    val syncMessage by viewModel.driveSyncMessage.collectAsStateWithLifecycle()
    val pendingImport by viewModel.pendingImportPayload.collectAsStateWithLifecycle()

    val labTests by viewModel.labTests.collectAsStateWithLifecycle()
    val bpRecords by viewModel.bpRecords.collectAsStateWithLifecycle()
    val sugarRecords by viewModel.sugarRecords.collectAsStateWithLifecycle()
    val weightRecords by viewModel.weightRecords.collectAsStateWithLifecycle()
    val medications by viewModel.medications.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val consultations by viewModel.consultations.collectAsStateWithLifecycle()

    var showHistory by remember { mutableStateOf(false) }

    // Launcher for selecting a backup JSON from Google Drive
    val importDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.inspectAndPrepareImport(context, uri) { error ->
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Launcher for directly creating/saving to a chosen Google Drive folder via Storage Access Framework
    var tempExportFile by remember { mutableStateOf<File?>(null) }
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                val outputStream = context.contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    viewModel.writeBackupToStream(
                        outputStream = outputStream,
                        onSuccess = { summary ->
                            Toast.makeText(
                                context,
                                "تم حفظ النسخة الاحتياطية بنجاح في Google Drive (${summary.totalRecordsCount} سجل)",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        onError = { err ->
                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            } catch (e: Exception) {
                Toast.makeText(context, "فشل حفظ الملف: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Main Google Drive Bento Card
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("google_drive_sync_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Google Drive Branding & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Google Drive Icon container with gradient
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF4285F4).copy(alpha = 0.15f),
                                        Color(0xFF0F9D58).copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF4285F4), Color(0xFF0F9D58))
                                ),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Google Drive",
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = strings.googleDriveTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF0F9D58).copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = strings.driveSyncBadge,
                                    fontSize = 10.sp,
                                    color = Color(0xFF0F9D58),
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                        Text(
                            text = strings.driveSyncSubtitle,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Data Summary Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.dataReadyForSync,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                val totalItems = labTests.size + bpRecords.size + sugarRecords.size + weightRecords.size + medications.size
                Text(
                    text = "$totalItems ${strings.recordsCount}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Syncing / Progress Indicator Banner
            AnimatedVisibility(visible = isSyncing) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp,
                        color = if (isAppInDarkTheme()) Color(0xFF64B5F6) else Color(0xFF1976D2)
                    )
                    Text(
                        text = syncMessage ?: strings.loading,
                        fontSize = 12.sp,
                        color = if (isAppInDarkTheme()) Color(0xFF64B5F6) else Color(0xFF1976D2),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // PRIMARY ACTION BUTTONS: EXPORT & IMPORT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: EXPORT TO GOOGLE DRIVE
                Button(
                    onClick = {
                        viewModel.exportAllDataToDrive(
                            context = context,
                            onReady = { file, summary ->
                                tempExportFile = file
                                GoogleDriveBackupManager.shareFileToGoogleDrive(context, file, summary)
                                Toast.makeText(
                                    context,
                                    strings.syncStatusSuccess,
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            onError = { err ->
                                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("export_to_google_drive_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.exportToDrive,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Button 2: IMPORT FROM GOOGLE DRIVE
                Button(
                    onClick = {
                        try {
                            importDocumentLauncher.launch(arrayOf("application/json", "*/*"))
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message ?: "", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("import_from_google_drive_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F9D58)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.importFromGoogleDrive,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Option: Export PDF Medical Report to Google Drive & Save As Dialog
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Save Directly to Specific Folder (SAF)
                OutlinedButton(
                    onClick = {
                        val dateSuffix = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                        createDocumentLauncher.launch("SmartHealth_Backup_$dateSuffix.json")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("save_as_drive_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.DriveFileMove, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.saveToFolder, fontSize = 11.5.sp)
                }

                // Export Full PDF to Google Drive
                OutlinedButton(
                    onClick = {
                        PdfReportExporter.generateAndShareMedicalPdf(
                            context = context,
                            labTests = labTests,
                            bpList = bpRecords,
                            sugarList = sugarRecords,
                            weightList = weightRecords,
                            consultation = consultations.firstOrNull(),
                            profile = userProfile,
                            targetDrive = true
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("export_pdf_to_drive_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MedicalRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.exportPdf, fontSize = 11.5.sp)
                }
            }

            // Sync History & Past Backups Section
            if (driveFiles.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showHistory = !showHistory }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${strings.syncHistoryTitle} (${driveFiles.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = if (showHistory) strings.hideText else strings.showText,
                        fontSize = 12.sp,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold
                    )
                }

                AnimatedVisibility(visible = showHistory) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        driveFiles.take(5).forEach { file ->
                            DriveSyncItemRow(
                                file = file,
                                onDelete = { viewModel.deleteDriveSyncFile(file) }
                            )
                        }

                        if (driveFiles.size > 1) {
                            TextButton(
                                onClick = { viewModel.clearDriveSyncHistory() },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.clearRecords, fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    // Import Preview / Confirmation Dialog
    pendingImport?.let { payload ->
        GoogleDriveImportDialog(
            payload = payload,
            onDismiss = { viewModel.dismissImportDialog() },
            onConfirmRestore = { mergeMode ->
                viewModel.executeRestoreImport(
                    mergeMode = mergeMode,
                    context = context,
                    onComplete = { result ->
                        if (result.isSuccess) {
                            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        )
    }
}

@Composable
private fun DriveSyncItemRow(
    file: DriveSyncFileEntity,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = null,
                tint = Color(0xFF0F9D58),
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = file.fileName,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "${file.syncDate} • ${file.fileSizeFormatted}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "حذف",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
