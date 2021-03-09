package com.example.androiddevchallenge

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonColors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlin.math.min

@Preview("Button", widthDp = 64, heightDp = 64)
@Composable
fun ButtonPreview() {
    CircularButton(
        Modifier.fillMaxSize(),
        {},
        true,
        MaterialTheme.colors.primary,
        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
    ) {
        Icon(Icons.Default.Stop, "Reset")
    }
}

@Preview("Graph", widthDp = 200, heightDp = 200)
@Composable
fun GraphPreview() {
    MyTheme {
        GraphicIndicator(
            Modifier.fillMaxSize(),
            0.18f,
            MaterialTheme.colors.primary
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
            textColor = MaterialTheme.colors.onSurface,
            0.87f
        )
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkTimerScreen() {
    MyTheme(darkTheme = true) {
        TimerScreen(
            Modifier.fillMaxSize(),
            TimerState.INIT,
            0,
            backgroundColor = MaterialTheme.colors.surface,
            primaryColor = MaterialTheme.colors.primary,
            textColor = MaterialTheme.colors.onSurface,
            0.87f
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
    fraction: Float,
    onStartClicked: () -> Unit = {},
    onPauseClicked: () -> Unit = {},
    onResetClicked: () -> Unit = {},
    onAddClicked: (Int) -> Unit = {},
    onRemoveClicked: (Int) -> Unit = {}
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

        GraphicIndicator(modifier = Modifier.size(200.dp), value = fraction, color = primaryColor)

        val disabledColor = textColor.copy(alpha = ContentAlpha.disabled)

        TimeSettingRow(timerState, seconds, primaryColor, disabledColor, onRemoveClicked, onAddClicked)
        ControlRow(timerState, seconds, primaryColor, disabledColor, onStartClicked, onPauseClicked, onResetClicked)
    }
}

@Composable
private fun GraphicIndicator(
    modifier: Modifier = Modifier,
    value: Float,
    color: Color
) {
    Canvas(modifier = modifier) {
        val sweep = value * 360

        val top = center.copy(y = center.y - min(center.x, center.y))

        drawLine(
            color = color,
            start = center,
            end = top
        )

        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = true
        )

        drawCircle(
            color = color,
            style = Stroke(width = 4f)
        )
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
        CircularButton(
            modifier = Modifier.size(64.dp),
            onClick = onResetClicked,
            enabled = timerState != TimerState.INIT,
            enabledColor = primaryColor,
            disabledColor = disabledColor,
        ) {
            Icon(Icons.Default.Stop, "Reset")
        }

        when (timerState) {
            TimerState.INIT, TimerState.DONE -> CircularButton(
                modifier = Modifier.size(64.dp),
                onClick = onStartClicked,
                enabled = seconds != 0,
                enabledColor = primaryColor,
                disabledColor = disabledColor,
            ) {
                Icon(Icons.Default.PlayArrow, "Start")
            }
            TimerState.RUNNING -> {
                CircularButton(
                    modifier = Modifier.size(64.dp),
                    onClick = onPauseClicked,
                    enabledColor = primaryColor,
                    disabledColor = disabledColor,
                ) {
                    Icon(Icons.Default.Pause, "Pause")
                }
            }
            TimerState.PAUSED -> {
                CircularButton(
                    modifier = Modifier.size(64.dp),
                    onClick = onStartClicked,
                    enabledColor = primaryColor,
                    disabledColor = disabledColor,
                ) {
                    Icon(Icons.Default.PlayArrow, "Cotinue")
                }
            }
        }
    }
}

@Composable
private fun TimeSettingRow(
    timerState: TimerState,
    seconds: Int,
    primaryColor: Color,
    disabledColor: Color,
    onRemoveClicked: (Int) -> Unit,
    onAddClicked: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CircularButton(
            Modifier.size(64.dp),
            onClick = { onRemoveClicked(10) },
            enabled = timerState == TimerState.INIT && seconds != 0,
            enabledColor = primaryColor,
            disabledColor = disabledColor,
        ) {
            Text("-10")
        }
        CircularButton(
            Modifier.size(64.dp),
            onClick = { onRemoveClicked(1) },
            enabled = timerState == TimerState.INIT && seconds != 0,
            enabledColor = primaryColor,
            disabledColor = disabledColor,
        ) {
            Text("-1")
        }
        CircularButton(
            Modifier.size(64.dp),
            onClick = { onAddClicked(1) },
            enabled = timerState == TimerState.INIT,
            enabledColor = primaryColor,
            disabledColor = disabledColor,
        ) {
            Text("+1")
        }
        CircularButton(
            Modifier.size(64.dp),
            onClick = { onAddClicked(10) },
            enabled = timerState == TimerState.INIT,
            enabledColor = primaryColor,
            disabledColor = disabledColor,
        ) {
            Text("+10")
        }
    }
}

@Composable
fun CircularButton(
    modifier: Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    enabledColor: Color,
    disabledColor: Color,
    content: @Composable() (RowScope.() -> Unit)
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
        colors = buttonColors,
        content = content
    )
}