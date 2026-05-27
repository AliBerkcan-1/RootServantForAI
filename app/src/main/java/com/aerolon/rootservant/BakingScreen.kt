package com.aerolon.rootservant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BakingScreen(
    bakingViewModel: BakingViewModel = viewModel()
) {

    var showDisclaimer by rememberSaveable { mutableStateOf(true) }
    var isApiKeySet by rememberSaveable { mutableStateOf(false) }
    var apiKey by rememberSaveable { mutableStateOf("") }


    if (showDisclaimer) {
        DisclaimerDialog(
            onAccept = { showDisclaimer = false }
        )
    } else {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F0F13),
                            Color(0xFF191730),
                            Color(0xFF0F0F13)
                        )
                    )
                )
        ) {

            AnimatedVisibility(
                visible = !isApiKeySet,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 },
                exit = fadeOut(tween(300)) + slideOutVertically(tween(300)) { -it / 2 }
            ) {
                ApiKeySetupScreen(
                    apiKey = apiKey,
                    onApiKeyChange = { apiKey = it },
                    onSubmit = { isApiKeySet = true }
                )
            }


            AnimatedVisibility(
                visible = isApiKeySet,
                enter = fadeIn(tween(500, delayMillis = 300)) + slideInVertically(tween(500, delayMillis = 300)) { it / 2 },
                exit = fadeOut(tween(300))
            ) {
                MainAiCommandScreen(apiKey = apiKey, bakingViewModel = bakingViewModel)
            }
        }
    }
}

@Composable
fun DisclaimerDialog(onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = {  },
        icon = {
            Icon(Icons.Filled.Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.error)
        },
        title = {
            Text(text = "STRICT DISCLAIMER", fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                text = "This application requires ROOT access and acts as an AI-driven system commander. " +
                        "It has the capability to modify, delete, or overwrite core system files.\n\n" +
                        "By proceeding, you acknowledge that YOU are solely responsible for any data loss, system corruption, " +
                        "or permanently bricked devices. The developer assumes ZERO liability. Use strictly at your own risk.",
                textAlign = TextAlign.Justify
            )
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("I Accept the Risks")
            }
        }
    )
}

@Composable
fun ApiKeySetupScreen(
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    onSubmit: () -> Unit
) {

    val uriHandler = LocalUriHandler.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0x33FFFFFF))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = "AI Sparkle",
                tint = Color(0xFFA8C7FA),
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Root Servant",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = "Powered by Gemini Intelligence",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFA8C7FA)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = apiKey,
                onValueChange = onApiKeyChange,
                label = { Text("Gemini API Key", color = Color.LightGray) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFA8C7FA),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))


            TextButton(
                onClick = {

                    uriHandler.openUri("https://aistudio.google.com/app/apikey")
                }
            ) {
                Text(
                    text = "Don't have an API Key? Get one for FREE",
                    color = Color(0xFFA8C7FA),
                    fontSize = 12.sp,
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSubmit,
                enabled = apiKey.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A25E1),
                    disabledContainerColor = Color.DarkGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Initialize System", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun MainAiCommandScreen(
    apiKey: String,
    bakingViewModel: BakingViewModel
) {
    var userPrompt by rememberSaveable { mutableStateOf("") }
    val uiState by bakingViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Surface(
                color = Color(0xFF1E1E24),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userPrompt,
                        onValueChange = { userPrompt = it },
                        placeholder = { Text("Ask AI to run a root command...", color = Color.Gray) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color(0xFFA8C7FA),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF2A2A35), RoundedCornerShape(24.dp))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    FloatingActionButton(
                        onClick = {
                            if (userPrompt.isNotBlank()) {
                                bakingViewModel.sendRootPrompt(apiKey, userPrompt)
                                userPrompt = ""
                            }
                        },
                        containerColor = Color(0xFFA8C7FA),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Rounded.Send, contentDescription = "Send Command", tint = Color(0xFF0F0F13))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Terminal Output",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA8C7FA)
            )

            Spacer(modifier = Modifier.height(16.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0x80000000))
                    .padding(16.dp)
            ) {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(
                        color = Color(0xFFA8C7FA),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    var consoleText = "System idle. Awaiting orders..."
                    var textColor = Color(0xFF4AF626)

                    when (uiState) {
                        is UiState.Error -> {
                            textColor = Color(0xFFFF5252)
                            consoleText = (uiState as UiState.Error).errorMessage
                        }
                        is UiState.Success -> {
                            textColor = Color(0xFF4AF626)
                            consoleText = (uiState as UiState.Success).outputText
                        }
                        else -> {}
                    }

                    Text(
                        text = consoleText,
                        color = textColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}