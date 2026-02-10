package com.comsys.handheld.uhf

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.comsys.handheld.ui.components.BrutalistLabeledTextBox
import com.comsys.handheld.ui.theme.BrutalistColors
import com.comsys.handheld.ui.theme.BrutalistTypography
import com.comsys.handheld.ui.theme.brutalistBorder
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UHFScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uhfManager = remember { UHFManager.getInstance() }
    val scope = rememberCoroutineScope()

    val tags by uhfManager.tags.collectAsState()
    val status by uhfManager.status.collectAsState()
    val isConnected by uhfManager.isConnected.collectAsState()

    var isScanning by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf<UHFManager.TagInfo?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Auto-stop scanning when the first tag is detected
    LaunchedEffect(tags.size) {
        if (isScanning && tags.size == 1) {
            // First tag detected, stop scanning
            isScanning = false
            uhfManager.stopInventory()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "RFID SCANNER",
                        style = BrutalistTypography.Header
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrutalistColors.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrutalistColors.BrightYellow,
                    titleContentColor = BrutalistColors.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Connection Status Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isConnected) BrutalistColors.BrightGreen
                        else BrutalistColors.BrightRed
                    )
                    .brutalistBorder()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isConnected) "CONECTADO" else "DESCONECTADO",
                            style = BrutalistTypography.ButtonLabel,
                            color = BrutalistColors.Black
                        )
                        Text(
                            text = status.uppercase(),
                            style = BrutalistTypography.NavLabel,
                            color = BrutalistColors.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(BrutalistColors.Black)
                            .brutalistBorder()
                            .clickable {
                                scope.launch {
                                    if (isConnected) {
                                        // Disconnect
                                        val result = uhfManager.disconnect()
                                        result.onFailure {
                                            errorMessage = it.message
                                        }
                                    } else {
                                        // Connect
                                        val result = uhfManager.initialize(context)
                                        result.onFailure {
                                            errorMessage = it.message
                                        }
                                    }
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = if (isConnected) "DESCONECTAR" else "CONECTAR",
                            style = BrutalistTypography.ButtonLabel,
                            color = BrutalistColors.White
                        )
                    }
                }
            }

            // Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Scan/Stop Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isScanning) BrutalistColors.BrightRed
                            else if (isConnected) BrutalistColors.BrightYellow
                            else BrutalistColors.White
                        )
                        .brutalistBorder()
                        .clickable(enabled = isConnected) {
                            if (isScanning) {
                                // Stop scanning
                                scope.launch {
                                    isScanning = false
                                    uhfManager.stopInventory()
                                }
                            } else {
                                // Start scanning
                                scope.launch {
                                    uhfManager.clearTags()
                                    isScanning = true
                                    val result = uhfManager.startInventory()
                                    result.onFailure {
                                        errorMessage = it.message
                                        isScanning = false
                                    }
                                }
                            }
                        }
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isScanning) "DETENER" else "ESCANEAR",
                        style = BrutalistTypography.ButtonLabel,
                        color = BrutalistColors.Black
                    )
                }

                // Clear Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (!isScanning && tags.isNotEmpty())
                                BrutalistColors.White
                            else BrutalistColors.White.copy(alpha = 0.5f)
                        )
                        .brutalistBorder()
                        .clickable(enabled = !isScanning && tags.isNotEmpty()) {
                            uhfManager.clearTags()
                        }
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LIMPIAR",
                        style = BrutalistTypography.ButtonLabel,
                        color = BrutalistColors.Black
                    )
                }
            }

            // Tags Counter
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrutalistColors.Black)
                    .brutalistBorder()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ETIQUETAS: ${tags.size}",
                        style = BrutalistTypography.Header,
                        color = BrutalistColors.White
                    )
                    if (isScanning) {
                        Text(
                            text = "[ ESCANEANDO... ]",
                            style = BrutalistTypography.NavLabel,
                            color = BrutalistColors.BrightYellow
                        )
                    }
                }
            }

            // Tags List
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(BrutalistColors.White)
                    .brutalistBorder()
            ) {
                if (tags.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "[ NO HAY ETIQUETAS ]",
                            style = BrutalistTypography.Header,
                            color = BrutalistColors.Black.copy(alpha = 0.3f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tags, key = { it.epc }) { tag ->
                            BrutalistTagCard(
                                tag = tag,
                                onClick = {
                                    selectedTag = tag
                                    showDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Tag Details Dialog
    if (showDialog && selectedTag != null) {
        BrutalistTagDetailsDialog(
            tag = selectedTag!!,
            onDismiss = { showDialog = false }
        )
    }

    // Error Message
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(3000)
            errorMessage = null
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrutalistColors.BrightRed)
                    .brutalistBorder()
                    .padding(16.dp)
            ) {
                Text(
                    text = error.uppercase(),
                    style = BrutalistTypography.ButtonLabel,
                    color = BrutalistColors.Black
                )
            }
        }
    }
}

@Composable
fun BrutalistTagCard(
    tag: UHFManager.TagInfo,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrutalistColors.White)
            .brutalistBorder()
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // EPC
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "EPC:",
                    style = BrutalistTypography.NavLabel,
                    color = BrutalistColors.Black
                )
                Text(
                    text = tag.epc,
                    style = BrutalistTypography.NavLabel,
                    color = BrutalistColors.Black
                )
            }

            // TID (if available)
            if (tag.tid.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TID:",
                        style = BrutalistTypography.NavLabel,
                        color = BrutalistColors.Black
                    )
                    Text(
                        text = tag.tid,
                        style = BrutalistTypography.NavLabel,
                        color = BrutalistColors.Black
                    )
                }
            }

            // RSSI and Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "RSSI: ${tag.rssi}",
                    style = BrutalistTypography.NavLabel,
                    color = BrutalistColors.Black
                )
                Text(
                    text = "×${tag.count}",
                    style = BrutalistTypography.NavLabel,
                    color = BrutalistColors.Black
                )
            }
        }
    }
}

@Composable
fun BrutalistTagDetailsDialog(
    tag: UHFManager.TagInfo,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrutalistColors.White)
                .brutalistBorder()
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrutalistColors.BrightYellow)
                        .brutalistBorder()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "DETALLES DE ETIQUETA",
                        style = BrutalistTypography.Header,
                        color = BrutalistColors.Black
                    )
                }

                // Tag Details using BrutalistLabeledTextBox
                BrutalistLabeledTextBox(
                    label = "EPC",
                    text = tag.epc
                )

                if (tag.tid.isNotEmpty()) {
                    BrutalistLabeledTextBox(
                        label = "TID",
                        text = tag.tid
                    )
                }

                BrutalistLabeledTextBox(
                    label = "RSSI",
                    text = tag.rssi
                )

                BrutalistLabeledTextBox(
                    label = "LECTURAS",
                    text = tag.count.toString()
                )

                // Close Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrutalistColors.Black)
                        .brutalistBorder()
                        .clickable(onClick = onDismiss)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CERRAR",
                        style = BrutalistTypography.ButtonLabel,
                        color = BrutalistColors.White
                    )
                }
            }
        }
    }
}
