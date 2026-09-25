package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SoyaDarkColorScheme = darkColorScheme(
    primary = CyanNeon,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = VioletPulse,
    onSecondary = Color(0xFF1E0061),
    secondaryContainer = Color(0xFF3B1599),
    onSecondaryContainer = Color(0xFFE2D6FF),
    tertiary = AmberGaze,
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF643F00),
    onTertiaryContainer = Color(0xFFFFDDB0),
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF3E4E68),
    outlineVariant = Color(0xFF22304A),
    error = CrimsonAlert,
    onError = Color.White
)

private val SoyaLightColorScheme = lightColorScheme(
    primary = Color(0xFF006877),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA1EFFF),
    onPrimaryContainer = Color(0xFF001F25),
    secondary = Color(0xFF5B38B6),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4DAFF),
    onSecondaryContainer = Color(0xFF1D0060),
    tertiary = Color(0xFF815600),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDDB1),
    onTertiaryContainer = Color(0xFF2A1800),
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFF8A99AD),
    outlineVariant = Color(0xFFC7D3E3),
    error = CrimsonAlert,
    onError = Color.White
)

@Composable
fun SoyaTheme(
    darkTheme: Boolean = true, // Default to futuristic dark theme for SOYA AI
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> SoyaDarkColorScheme
        else -> SoyaLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = SoyaTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
