/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.androiddevchallenge.ui.theme.DarkColorPalette
import com.example.androiddevchallenge.ui.theme.LightColorPalette
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

enum class TimerState { INIT, RUNNING, PAUSED, DONE }

enum class ColorState { NORMAL, INVERTED }

// Start building your app here!
@Composable
fun MyApp(darkTheme: Boolean = isSystemInDarkTheme()) {
    val initialTime = remember { mutableStateOf(0) }
    val seconds = remember { mutableStateOf(0) }
    val millis = remember { mutableStateOf(0L) }

    val state = remember { mutableStateOf(TimerState.INIT) }
    val colorState = remember { mutableStateOf(ColorState.NORMAL) }

    val scope = rememberCoroutineScope()

    val job = remember { mutableStateOf<Job?>(null) }

    val primaryPallette = if (darkTheme) DarkColorPalette else LightColorPalette
    val invertedPallette = if (darkTheme) LightColorPalette else DarkColorPalette

    val backgroundColor by animateColorAsState(if (colorState.value == ColorState.NORMAL) primaryPallette.surface else invertedPallette.surface)
    val primaryColor by animateColorAsState(if (colorState.value == ColorState.NORMAL) primaryPallette.primary else invertedPallette.primary)
    val textColor by animateColorAsState(if (colorState.value == ColorState.NORMAL) primaryPallette.onSurface else invertedPallette.onSurface)

    val fraction = remember { mutableStateOf(0f) }

    TimerScreen(
        modifier = Modifier.fillMaxSize(),
        timerState = state.value,
        seconds = seconds.value,
        backgroundColor = backgroundColor,
        primaryColor = primaryColor,
        textColor = textColor,
        fraction = fraction.value,
        onAddClicked = { onTimeChange(it, seconds, fraction) },
        onRemoveClicked = { onTimeChange(-it, seconds, fraction) },
        onStartClicked = { onStartClicked(scope, job, state, colorState, initialTime, seconds, millis, fraction) },
        onPauseClicked = { onPauseClicked(job.value, colorState, state) },
        onResetClicked = { onResetClicked(initialTime.value, job.value, seconds, colorState, state, fraction) }
    )
}

fun onStartClicked(
    scope: CoroutineScope,
    job: MutableState<Job?>,
    state: MutableState<TimerState>,
    colorState: MutableState<ColorState>,
    initialTime: MutableState<Int>,
    seconds: MutableState<Int>,
    millis: MutableState<Long>,
    fraction: MutableState<Float>,
) {
    colorState.value = ColorState.NORMAL
    if (state.value == TimerState.INIT) {
        initialTime.value = seconds.value
        millis.value = seconds.value * 1000L
    }

    state.value = TimerState.RUNNING
    job.value = scope.launch {
        fraction.value = seconds.value.toFloat() / initialTime.value

        var startMillis = System.currentTimeMillis()
        try {
            while (millis.value > 0L) {
                delay(10)
                val tickMillis = System.currentTimeMillis()
                millis.value -= tickMillis - startMillis
                millis.value = millis.value.coerceAtLeast(0L)

                startMillis = tickMillis
                seconds.value = if (millis.value == 0L) 0 else ((millis.value + 1000) / 1000).toInt()
                fraction.value = seconds.value.toFloat() / initialTime.value
            }
            state.value = TimerState.DONE

            while (true) {
                colorState.value = if (colorState.value == ColorState.NORMAL) ColorState.INVERTED else ColorState.NORMAL
                delay(500)
            }
        } catch (e: CancellationException) {
            val cancelMillis = System.currentTimeMillis()
            millis.value -= cancelMillis - startMillis
        }
    }
}

fun onTimeChange(change: Int, seconds: MutableState<Int>, fraction: MutableState<Float>) {
    val newValue = seconds.value + change
    seconds.value = newValue.coerceIn(0..3599)
    fraction.value = seconds.value % 60 / 60f
}

fun onPauseClicked(job: Job?, colorState: MutableState<ColorState>, timerState: MutableState<TimerState>) {
    job?.cancel()
    colorState.value = ColorState.INVERTED
    timerState.value = TimerState.PAUSED
}

fun onResetClicked(
    initialTime: Int,
    job: Job?,
    seconds: MutableState<Int>,
    colorState: MutableState<ColorState>,
    timerState: MutableState<TimerState>,
    fraction: MutableState<Float>
) {
    job?.cancel()
    seconds.value = initialTime
    colorState.value = ColorState.NORMAL
    timerState.value = TimerState.INIT
    fraction.value = seconds.value % 60 / 60f
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyApp()
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyApp(darkTheme = true)
}
