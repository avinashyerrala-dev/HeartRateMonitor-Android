package com.health.heartratemonitor.presentation.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

@Composable
fun PermissionManager(
    onAllPermissionsGranted: @Composable () -> Unit
) {
    /* full set once ------------------------------------------------------- */
    val requestedPermissions = remember {
        buildList {
            add(Manifest.permission.BLUETOOTH_SCAN)
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val context = LocalContext.current

    /* state holders ------------------------------------------------------- */
    var permanentlyDenied by remember { mutableStateOf<List<String>>(emptyList()) }
    var needRationale     by remember { mutableStateOf<List<String>>(emptyList()) }

    /* launcher ------------------------------------------------------------ */
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultMap ->
        val denied = resultMap.filterValues { !it }.keys.toList()

        permanentlyDenied = denied.filter { perm ->
            !ActivityCompat.shouldShowRequestPermissionRationale(
                context.findActivity()!!,
                perm
            )
        }
        needRationale = denied - permanentlyDenied

        if (denied.isEmpty()) {
            permanentlyDenied = emptyList()
            needRationale     = emptyList()
        }
    }


    /* first composition --------------------------------------------------- */
    LaunchedEffect(Unit) {
        val notGranted = requestedPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PERMISSION_GRANTED
        }
        if (notGranted.isEmpty()) return@LaunchedEffect

        launcher.launch(notGranted.toTypedArray())
    }

    /* UI branch ----------------------------------------------------------- */
    when {
        permanentlyDenied.isEmpty() && needRationale.isEmpty() -> {
            onAllPermissionsGranted()
        }
        else -> {
            PermissionDeniedScreen(
                permanentlyDenied = permanentlyDenied,
                needRationale     = needRationale,
                onRequestAgain    = { launcher.launch((needRationale).toTypedArray()) },
                onOpenSettings    = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }
            )
        }
    }
}
@SuppressLint("RestrictedApi")
private tailrec fun Context.findActivity(): ComponentActivity? =
    when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.findActivity()
        else                 -> null
    }


