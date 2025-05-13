package com.health.heartratemonitor.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HRMScaffold(
    title: String,
    showOverlay: Boolean,
    onToggleOverlay: () -> Unit,
    overlayContent: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (Modifier) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden }
    )

    if (showOverlay) {
        ModalBottomSheet(
            onDismissRequest = onToggleOverlay,
            sheetState = sheetState
        ) {
            overlayContent()
        }
    }

    Scaffold(
        topBar = {
            HRMTopBar(title = title, onDropdownClick = onToggleOverlay)
        },
        bottomBar = bottomBar
    ) { padding ->
        content(Modifier.padding(padding))
    }
}
