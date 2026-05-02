package com.example.farmeradvisoryapp.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.farmeradvisoryapp.models.ChatMessage
import com.example.farmeradvisoryapp.navigation.Screen
import com.example.farmeradvisoryapp.ui.components.BottomNavigationBar
import com.example.farmeradvisoryapp.viewmodels.ExpertUiState
import com.example.farmeradvisoryapp.viewmodels.ExpertViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertScreen(navController: NavController, viewModel: ExpertViewModel = hiltViewModel()) {
    var messageText by remember { mutableStateOf("") }
    val selectedImage by viewModel.selectedImage.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val isLoading = uiState is ExpertUiState.Loading

    val galleryLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
                viewModel.setSelectedImage(bitmap)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let { viewModel.setSelectedImage(it) }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(messages.size - 1) }
        }
    }

    val geminiGradient = Brush.linearGradient(colors = listOf(Color(0xFF4285F4), Color(0xFF9B51E0)))

    Scaffold(
        topBar = { ExpertTopBar(navController) { viewModel.clearMessages() } },
        bottomBar = { BottomNavigationBar(navController, Screen.Expert.route) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (messages.isEmpty()) {
                    item { IntroScreen(onTopicClick = { messageText = it }) }
                }
                items(messages) { message -> ChatBubble(message) }
                if (isLoading) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Gemini is thinking...", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            if (uiState is ExpertUiState.Error) {
                val errorMsg = (uiState as ExpertUiState.Error).message
                Snackbar(
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter),
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ) { Text(errorMsg) }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 16.dp)
            ) {
                AnimatedVisibility(visible = selectedImage != null) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).size(100.dp).clip(RoundedCornerShape(12.dp))) {
                        selectedImage?.let {
                            androidx.compose.foundation.Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        IconButton(
                            onClick = { viewModel.setSelectedImage(null) },
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { galleryLauncher.launch("image/*") }, enabled = !isLoading) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(onClick = { cameraLauncher.launch(null) }, enabled = !isLoading) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }

                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            placeholder = { Text("Ask for farming advice...", color = Color.Gray) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            enabled = !isLoading,
                            maxLines = 3
                        )

                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendMessage(messageText)
                                    messageText = ""
                                }
                            },
                            enabled = !isLoading && messageText.isNotBlank(),
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isLoading || messageText.isBlank()) {
                                        Modifier.background(Color.Gray)
                                    } else {
                                        Modifier.background(geminiGradient)
                                    }
                                )
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertTopBar(navController: NavController, onClear: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gemini AI Expert", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        },
        actions = {
            IconButton(onClick = onClear) {
                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (message.isUser) 20.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 20.dp
            ),
            color = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            tonalElevation = if (message.isUser) 0.dp else 2.dp
        ) {
            Text(
                text = message.text, 
                modifier = Modifier.padding(12.dp),
                color = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun IntroScreen(onTopicClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🌾", fontSize = 64.sp, modifier = Modifier.padding(bottom = 16.dp))
        Text("Farming Expert AI", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text("Ask questions about crops, weather, pests, and more", color = Color.Gray, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp, bottom = 24.dp))

        val suggestedQuestions = listOf("How to increase wheat yield?", "Best time to plant rice", "Pest control for cotton", "Soil health improvement")
        suggestedQuestions.forEach { question ->
            SuggestedQuestionButton(text = question) { onTopicClick(question) }
        }
    }
}

@Composable
fun SuggestedQuestionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
    }
}
