package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingBottomNavigation
import com.example.ui.components.NavItem
import com.example.ui.components.PrimaryGradientButton
import com.example.ui.screens.AiAdvisorScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DailyTrackingScreen
import com.example.ui.screens.HealthProfileScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LabTestsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.ResultsVitalsScreen
import com.example.ui.theme.EmergencyRedGradient
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.MedicalRedSoft
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MedicalViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.CredentialManager
import com.example.ui.locale.LocalAppLanguage
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.getAppStrings
import com.example.util.attemptAutoSignIn
import com.example.util.authStateFlow
import com.example.util.configureAppCheck
import com.example.util.signOut
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

enum class ActiveSubScreen {
    NONE,
    RESULTS,
    MEDICATIONS,
    AI_ADVISOR,
    HEALTH_PROFILE,
    LAB_TESTS_MANAGER
}

class MainActivity : ComponentActivity() {

    private val viewModel: MedicalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        try {
            enableEdgeToEdge()
        } catch (_: Throwable) {
        }

        // Ensure notification channel is registered on startup
        try {
            com.example.util.MedicationReminderScheduler.createNotificationChannelIfNeeded(this)
        } catch (_: Throwable) {
        }

        setContent {
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val darkModeOverride by viewModel.darkModeOverride.collectAsStateWithLifecycle()
            val isDarkMode = darkModeOverride ?: systemDark

            val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
            val effectiveLanguage = appLanguage.resolveEffectiveLanguage()
            val appStrings = getAppStrings(appLanguage)

            LaunchedEffect(appStrings.appName) {
                try {
                    title = appStrings.appName
                } catch (_: Throwable) {
                }
            }

            MyApplicationTheme(
                darkTheme = isDarkMode,
                isArabic = effectiveLanguage == com.example.ui.locale.AppLanguage.AR
            ) {
                // Support dynamic layout direction (RTL for Arabic, LTR for English/Russian)
                CompositionLocalProvider(
                    LocalAppLanguage provides effectiveLanguage,
                    LocalAppStrings provides appStrings,
                    LocalLayoutDirection provides effectiveLanguage.layoutDirection
                ) {
                    AppRoot(
                        viewModel = viewModel,
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { viewModel.setDarkMode(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun AppRoot(
    viewModel: MedicalViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val activity = (context as? android.app.Activity)
    val coroutineScope = rememberCoroutineScope()

    val auth = remember(context) {
        try {
            com.example.util.ensureFirebaseInitialized(context)
            Firebase.auth
        } catch (_: Throwable) {
            null
        }
    }
    val authFlow = remember(auth) {
        auth?.authStateFlow() ?: kotlinx.coroutines.flow.flowOf(null)
    }
    val initialUser = remember(auth) {
        try {
            auth?.currentUser
        } catch (_: Throwable) {
            null
        }
    }
    val currentUser by authFlow.collectAsStateWithLifecycle(initialValue = initialUser)
    val credentialManager = remember(context) {
        try {
            CredentialManager.create(context)
        } catch (_: Throwable) {
            null
        }
    }

    var selectedTab by rememberSaveable { mutableStateOf(NavItem.HOME) }
    var activeSubScreen by rememberSaveable { mutableStateOf(ActiveSubScreen.NONE) }
    var showUrgentEmergencyDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val openTab = try {
            activity?.intent?.getStringExtra("open_tab")
        } catch (_: Throwable) {
            null
        }
        if (openTab == "medications") {
            selectedTab = NavItem.DAILY
        }
        if (currentUser == null) {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {},
                onUnauthenticated = {},
                scope = coroutineScope
            )
        }
    }

    LaunchedEffect(currentUser?.uid) {
        if (currentUser != null) {
            viewModel.syncSignedInUser(currentUser)
            // Request POST_NOTIFICATIONS permission on Android 13+ after sign-in
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU && activity != null) {
                    if (androidx.core.content.ContextCompat.checkSelfPermission(
                            activity,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        androidx.core.app.ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            1001
                        )
                    }
                }
            } catch (_: Throwable) {
            }
        }
    }

    val user = currentUser
    if (user == null) {
        AuthScreen(
            onAuthSuccess = {
                // Firebase AuthStateListener reactively transitions into authenticated UI
            }
        )
    } else {
        key(user.uid) {
            // Handle Back Button
            if (activeSubScreen != ActiveSubScreen.NONE) {
                BackHandler {
                    activeSubScreen = ActiveSubScreen.NONE
                }
            } else if (selectedTab != NavItem.HOME) {
                BackHandler {
                    selectedTab = NavItem.HOME
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        FloatingBottomNavigation(
                            selectedItem = selectedTab,
                            onItemSelected = { item ->
                                activeSubScreen = ActiveSubScreen.NONE
                                selectedTab = item
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = Pair(selectedTab, activeSubScreen),
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "screen_transition"
                        ) { (tab, subScreen) ->
                            when {
                                subScreen == ActiveSubScreen.RESULTS -> {
                                    ResultsVitalsScreen(viewModel = viewModel)
                                }
                                subScreen == ActiveSubScreen.MEDICATIONS -> {
                                    DailyTrackingScreen(viewModel = viewModel)
                                }
                                subScreen == ActiveSubScreen.AI_ADVISOR -> {
                                    AiAdvisorScreen(viewModel = viewModel)
                                }
                                subScreen == ActiveSubScreen.HEALTH_PROFILE -> {
                                    HealthProfileScreen(viewModel = viewModel)
                                }
                                subScreen == ActiveSubScreen.LAB_TESTS_MANAGER -> {
                                    LabTestsScreen(
                                        viewModel = viewModel,
                                        onNavigateToAiAdvisor = { activeSubScreen = ActiveSubScreen.AI_ADVISOR }
                                    )
                                }
                                else -> {
                                    when (tab) {
                                        NavItem.HOME -> {
                                            HomeScreen(
                                                viewModel = viewModel,
                                                onNavigateToDailyTracking = { selectedTab = NavItem.DAILY },
                                                onNavigateToResults = { selectedTab = NavItem.RESULTS },
                                                onNavigateToAiAdvisor = { selectedTab = NavItem.AI_ADVISOR },
                                                onNavigateToProfile = { selectedTab = NavItem.PROFILE },
                                                onNavigateToLabTests = { activeSubScreen = ActiveSubScreen.LAB_TESTS_MANAGER }
                                            )
                                        }

                                        NavItem.DAILY -> {
                                            DailyTrackingScreen(viewModel = viewModel)
                                        }

                                        NavItem.RESULTS -> {
                                            ResultsVitalsScreen(
                                                viewModel = viewModel,
                                                onNavigateToLabTests = { activeSubScreen = ActiveSubScreen.LAB_TESTS_MANAGER }
                                            )
                                        }

                                        NavItem.AI_ADVISOR -> {
                                            AiAdvisorScreen(viewModel = viewModel)
                                        }

                                        NavItem.PROFILE -> {
                                            ProfileSettingsScreen(
                                                viewModel = viewModel,
                                                isDarkMode = isDarkMode,
                                                onToggleDarkMode = onToggleDarkMode,
                                                onLogout = {
                                                    selectedTab = NavItem.HOME
                                                    activeSubScreen = ActiveSubScreen.NONE
                                                    signOut(
                                                        credentialManager = credentialManager,
                                                        scope = coroutineScope
                                                    )
                                                },
                                                onNavigateToHealthProfile = { activeSubScreen = ActiveSubScreen.HEALTH_PROFILE }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Urgent Consultation Modal Sheet / Dialog
                if (showUrgentEmergencyDialog) {
                    EmergencyConsultationModal(
                        onDismiss = { showUrgentEmergencyDialog = false },
                        onCallEmergency = {
                            showUrgentEmergencyDialog = false
                            Toast.makeText(context, "جاري توصيلك بطبيب الطوارئ المناوب...", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyConsultationModal(
    onDismiss: () -> Unit,
    onCallEmergency: () -> Unit
) {
    val strings = LocalAppStrings.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(EmergencyRedGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = strings.emergencyTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = strings.emergencySubtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = strings.emergencySubtitle,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.call123,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {
            PrimaryGradientButton(
                text = strings.call123,
                icon = Icons.Default.Phone,
                onClick = onCallEmergency,
                testTag = "confirm_emergency_call_btn"
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.cancel, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
