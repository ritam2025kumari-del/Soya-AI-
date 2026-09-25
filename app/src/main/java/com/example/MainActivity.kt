package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SoyaMainViewModel
import com.example.ui.screens.AssistantScreen
import com.example.ui.screens.AutomationHubScreen
import com.example.ui.screens.MemoryVaultScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VoiceStudioScreen
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.SoyaTheme
import com.example.ui.theme.VioletPulse

enum class SoyaNavScreen(
    val route: String,
    val title: String,
    val hindiTitle: String,
    val icon: ImageVector
) {
    ASSISTANT("assistant", "SOYA AI", "पर्सनल असिस्टेंट", Icons.Default.Psychology),
    VOICE_STUDIO("voice_studio", "Voice", "हिंदी आवाज़", Icons.Default.RecordVoiceOver),
    AUTOMATION("automation", "Automation", "ऑटोमेशन", Icons.Default.Bolt),
    MEMORY("memory", "Memory", "स्मृति कोष", Icons.Default.Memory),
    SETTINGS("settings", "Settings", "सेटिंग्स", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoyaTheme {
                SoyaMainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoyaMainApp(viewModel: SoyaMainViewModel = viewModel()) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(SoyaNavScreen.ASSISTANT) }
    val toastMsg by viewModel.toastMessage.collectAsState()

    // Handle Back Button
    BackHandler(enabled = currentScreen != SoyaNavScreen.ASSISTANT) {
        currentScreen = SoyaNavScreen.ASSISTANT
    }

    // Toast listener
    LaunchedEffect(toastMsg) {
        toastMsg?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SOYA AI • ${currentScreen.title}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                SoyaNavScreen.entries.forEach { screen ->
                    val isSelected = (currentScreen == screen)
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = CyanNeon,
                            indicatorColor = CyanNeon,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            when (currentScreen) {
                SoyaNavScreen.ASSISTANT -> AssistantScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
                SoyaNavScreen.VOICE_STUDIO -> VoiceStudioScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
                SoyaNavScreen.AUTOMATION -> AutomationHubScreen(
                    viewModel = viewModel,
                    onNavigateToChat = { currentScreen = SoyaNavScreen.ASSISTANT },
                    modifier = Modifier.fillMaxSize()
                )
                SoyaNavScreen.MEMORY -> MemoryVaultScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
                SoyaNavScreen.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
