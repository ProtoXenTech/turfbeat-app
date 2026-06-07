package turfbeat.com.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Pitch,
    onPrimary = Surface,
    primaryContainer = PitchSoft,
    onPrimaryContainer = Forest,
    secondary = Panel,
    onSecondary = Ink,
    tertiary = Forest,
    background = Bg,
    onBackground = Ink,
    surface = Surface,
    onSurface = Ink,
    surfaceVariant = Panel,
    onSurfaceVariant = Ink2,
    outline = Line,
    outlineVariant = Line2,
    error = Error,
    onError = Surface,
    errorContainer = ErrorBg,
    onErrorContainer = Error
)

private val DarkColorScheme = darkColorScheme(
    primary = Pitch2,
    onPrimary = Forest,
    primaryContainer = Forest2,
    onPrimaryContainer = PitchSoft,
    secondary = Ink2,
    onSecondary = Panel,
    tertiary = Pitch,
    background = Ink,
    onBackground = Panel,
    surface = Ink,
    onSurface = Panel,
    surfaceVariant = Ink2,
    onSurfaceVariant = Line,
    outline = Ink3,
    outlineVariant = Ink2,
    error = Error,
    onError = Surface,
    errorContainer = ErrorBg,
    onErrorContainer = Error
)

@Composable
fun TurfBeatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TurfBeatTypography,
        content = content
    )
}
