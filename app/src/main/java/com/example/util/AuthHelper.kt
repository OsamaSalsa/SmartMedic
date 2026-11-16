package com.example.util

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val AUTH_TAG = "Auth"
private const val FALLBACK_WEB_CLIENT_ID = "201721039668-pol9vgrsh3o6634fkod47pfp9ku167ma.apps.googleusercontent.com"

private fun resolveWebClientId(context: Context): String {
    return try {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (resId != 0) {
            context.getString(resId).ifBlank { FALLBACK_WEB_CLIENT_ID }
        } else {
            FALLBACK_WEB_CLIENT_ID
        }
    } catch (t: Throwable) {
        Log.w(AUTH_TAG, "Using fallback web client ID", t)
        FALLBACK_WEB_CLIENT_ID
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    try {
        addAuthStateListener(listener)
    } catch (t: Throwable) {
        Log.e(AUTH_TAG, "Failed to attach AuthStateListener", t)
        trySend(null)
    }
    awaitClose {
        try {
            removeAuthStateListener(listener)
        } catch (_: Throwable) {
        }
    }
}

fun attemptAutoSignIn(
    context: Context,
    credentialManager: CredentialManager?,
    onAuthSuccess: () -> Unit,
    onUnauthenticated: () -> Unit,
    scope: CoroutineScope
) {
    try {
        ensureFirebaseInitialized(context)
        if (Firebase.auth.currentUser != null) {
            onAuthSuccess()
            return
        }
        val activity = context as? Activity
        if (activity == null || activity.isFinishing || activity.isDestroyed || credentialManager == null) {
            onUnauthenticated()
            return
        }

        val clientId = resolveWebClientId(context)
        if (clientId.isBlank()) {
            onUnauthenticated()
            return
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    Firebase.auth.signInWithCredential(authCredential).await()
                    onAuthSuccess()
                } else {
                    onUnauthenticated()
                }
            } catch (e: NoCredentialException) {
                Log.d(AUTH_TAG, "No saved Google credentials for auto sign-in: ${e.message}")
                onUnauthenticated()
            } catch (e: CancellationException) {
                throw e
            } catch (t: Throwable) {
                Log.d(AUTH_TAG, "Silent auto sign-in skipped: ${t.message}")
                onUnauthenticated()
            }
        }
    } catch (t: Throwable) {
        Log.w(AUTH_TAG, "Auto sign-in setup skipped: ${t.message}")
        onUnauthenticated()
    }
}

fun onGoogleSignInClicked(
    context: Context,
    credentialManager: CredentialManager?,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    scope: CoroutineScope,
    onAuthCancelled: () -> Unit = {}
) {
    try {
        ensureFirebaseInitialized(context)
        val isAr = java.util.Locale.getDefault().language == "ar"
        val isRu = java.util.Locale.getDefault().language == "ru"
        val genericFailMsg = when {
            isAr -> "تعذر تسجيل الدخول حالياً. يرجى المحاولة مرة أخرى."
            isRu -> "Не удалось войти. Пожалуйста, попробуйте еще раз."
            else -> "Unable to sign in right now. Please try again."
        }
        val noAccountMsg = when {
            isAr -> "لم يتم العثور على حساب Google على هذا الجهاز. يرجى إضافة حساب من إعدادات الهاتف."
            isRu -> "Аккаунт Google не найден на устройстве. Добавьте аккаунт в настройках телефона."
            else -> "No Google account found on this device. Please add a Google account in Settings."
        }

        val clientId = resolveWebClientId(context)
        if (clientId.isBlank() || credentialManager == null) {
            onAuthError(genericFailMsg)
            return
        }

        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        scope.launch {
            try {
                val activityContext = (context as? Activity) ?: context
                val result = credentialManager.getCredential(activityContext, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    Firebase.auth.signInWithCredential(authCredential).await()
                    onAuthSuccess()
                } else {
                    onAuthError(genericFailMsg)
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w(AUTH_TAG, "Google Sign-In flow cancelled or dismissed: ${e.message}", e)
                onAuthCancelled()
            } catch (e: NoCredentialException) {
                Log.w(AUTH_TAG, "No Google credential available on device: ${e.message}", e)
                onAuthError(noAccountMsg)
            } catch (e: CancellationException) {
                throw e
            } catch (t: Throwable) {
                Log.e(AUTH_TAG, "Google Sign-In failed", t)
                onAuthError(genericFailMsg)
            }
        }
    } catch (t: Throwable) {
        Log.e(AUTH_TAG, "Google Sign-In initialization failed", t)
        val isAr = java.util.Locale.getDefault().language == "ar"
        val isRu = java.util.Locale.getDefault().language == "ru"
        onAuthError(
            when {
                isAr -> "تعذر تسجيل الدخول حالياً. يرجى المحاولة مرة أخرى."
                isRu -> "Не удалось войти. Пожалуйста, попробуйте еще раз."
                else -> "Unable to sign in right now. Please try again."
            }
        )
    }
}

fun signOut(
    credentialManager: CredentialManager?,
    scope: CoroutineScope,
    onSignOutComplete: () -> Unit = {}
) {
    scope.launch {
        withContext(NonCancellable) {
            try {
                credentialManager?.clearCredentialState(ClearCredentialStateRequest())
            } catch (t: Throwable) {
                Log.w(AUTH_TAG, "Failed to clear credential state: ${t.message}")
            } finally {
                try {
                    Firebase.auth.signOut()
                } catch (_: Throwable) {
                }
                onSignOutComplete()
            }
        }
    }
}
