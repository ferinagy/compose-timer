package com.example.androiddevchallenge

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExposureNeg1
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme

@Preview("Button", widthDp = 64, heightDp = 64)
@Composable
fun ButtonPreview() {
    MyTheme {
        CircularButton(
            Modifier.fillMaxSize(),
            {},
            Icons.Default.PlayArrow,
            "Play",
            true,
            MaterialTheme.colors.primary,
            MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LigthTimerScreen() {
    MyTheme {
        TimerScreen(
            Modifier.fillMaxSize(),
            TimerState.INIT,
            68,
            backgroundColor = MaterialTheme.colors.surface,
            primaryColor = MaterialTheme.colors.primary,
            textColor = MaterialTheme.colors.onSurface
        )
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkTimerScreen() {
    MyTheme(darkTheme = true) {
        TimerScreen(Modifier.fillMaxSize(),
            TimerState.INIT,
            0,
            backgroundColor = MaterialTheme.colors.surface,
            primaryColor = MaterialTheme.colors.primary,
            textColor = MaterialTheme.colors.onSurface
        )
    }
}


@Composable
fun TimerScreen(
    modifier: Modifier,
    timerState: TimerState,
    seconds: Int,
    backgroundColor: Color,
    primaryColor: Color,
    textColor: Color,
    onStartClicked: () -> Unit = {},
    onPauseClicked: () -> Unit = {},
    onResetClicked: () -> Unit = {},
    onAddClicked: () -> Unit = {},
    onRemoveClicked: () -> Unit = {}
) {
    val text = remember(seconds) { "%02d:%02d".format(seconds / 60, seconds % 60) }
    Column(
        modifier = modifier.background(color = backgroundColor),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.h1
        )

        val disabledColor = textColor.copy(alpha = ContentAlpha.disabled)

        TimeSettingRow(timerState, primaryColor, disabledColor, onRemoveClicked, onAddClicked)
        ControlRow(timerState, seconds, primaryColor, disabledColor, onStartClicked, onPauseClicked, onResetClicked)
    }
}

@Composable
private fun ControlRow(
    timerState: TimerState,
    seconds: Int,
    primaryColor: Color,
    disabledColor: Color,
    onStartClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onResetClicked: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        when (timerState) {
            TimerState.INIT -> CircularButton(
                modifier = Modifier.size(64.dp),
                onClick = onStartClicked,
                icon = Icons.Default.PlayArrow,
                contentDescription = "Start",
                enabled = seconds != 0,
                enabledColor = primaryColor,
                disabledColor = disabledColor
            )
            TimerState.RUNNING -> {
                ResetButton(onResetClicked, primaryColor, disabledColor)
                CircularButton(
                    modifier = Modifier.size(64.dp),
                    onClick = onPauseClicked,
                    icon = Icons.Default.Pause,
                    contentDescription = "Pause",
                    enabledColor = primaryColor,
                    disabledColor = disabledColor
                )
            }
            TimerState.PAUSED -> {
                ResetButton(onResetClicked, primaryColor, disabledColor)
                CircularButton(
                    modifier = Modifier.size(64.dp),
                    onClick = onStartClicked,
                    icon = Icons.Default.PlayArrow,
                    contentDescription = "Continue",
                    enabledColor = primaryColor,
                    disabledColor = disabledColor
                )
            }
            TimerState.DONE -> ResetButton(onResetClicked, primaryColor, disabledColor)
        }
    }
}

@Composable
private fun ResetButton(onClick: () -> Unit, primaryColor: Color, disabledColor: Color) {
    CircularButton(
        modifier = Modifier.size(64.dp),
        onClick = onClick,
        icon = Icons.Default.Stop,
        contentDescription = "Reset",
        enabledColor = primaryColor,
        disabledColor = disabledColor
    )
}

@Composable
private fun TimeSettingRow(
    timerState: TimerState,
    primaryColor: Color,
    disabledColor: Color,
    onRemoveClicked: () -> Unit,
    onAddClicked: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CircularButton(
            Modifier.size(64.dp),
            onClick = onRemoveClicked,
            Icons.Default.ExposureNeg1,
            "Remove a second",
            enabled = timerState == TimerState.INIT,
            enabledColor = primaryColor,
            disabledColor = disabledColor
        )
        CircularButton(
            Modifier.size(64.dp),
            onClick = onAddClicked,
            Icons.Default.ExposurePlus1,
            "Add a second",
            enabled = timerState == TimerState.INIT,
            enabledColor = primaryColor,
            disabledColor = disabledColor
        )
    }
}

@Composable
fun CircularButton(
    modifier: Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean = true,
    enabledColor: Color,
    disabledColor: Color
) {
    val contentColor = if (enabled) enabledColor else disabledColor
    val buttonColors = object : ButtonColors {

        @Composable
        override fun backgroundColor(enabled: Boolean): State<Color> {
            return mutableStateOf(Color.Transparent)
        }

        @Composable
        override fun contentColor(enabled: Boolean): State<Color> {
            return rememberUpdatedState(if (enabled) enabledColor else disabledColor)
        }

    }
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape,
        enabled = enabled,
        border = BorderStroke(width = 2.dp, color = contentColor),
        colors = buttonColors
    ) {
        Icon(icon, contentDescription)
    }
}