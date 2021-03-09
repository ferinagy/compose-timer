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
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.androiddevchallenge.ui.theme.DarkColorPalette
import com.example.androiddevchallenge.ui.theme.LightColorPalette
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.CancellationException
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
    var initialTime by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(0) }
    var millis by remember { mutableStateOf(0L) }


    var state by remember { mutableStateOf(TimerState.INIT) }
    var colorState by remember { mutableStateOf(ColorState.NORMAL) }

    val scope = rememberCoroutineScope()

    var job by remember { mutableStateOf<Job?>(null) }

    val primaryPallette = if (darkTheme) DarkColorPalette else LightColorPalette
    val invertedPallette = if (darkTheme) LightColorPalette else DarkColorPalette

    val backgroundColor by animateColorAsState(if (colorState == ColorState.NORMAL) primaryPallette.surface else invertedPallette.surface)
    val primaryColor by animateColorAsState(if (colorState == ColorState.NORMAL) primaryPallette.primary else invertedPallette.primary)
    val textColor by animateColorAsState(if (colorState == ColorState.NORMAL) primaryPallette.onSurface else invertedPallette.onSurface)

    TimerScreen(
        modifier = Modifier.fillMaxSize(),
        state,
        seconds = seconds,
        backgroundColor = backgroundColor,
        primaryColor = primaryColor,
        textColor = textColor,
        onAddClicked = { seconds += it },
        onRemoveClicked = {
            seconds -= it
            seconds = seconds.coerceAtLeast(0)
        },
        onStartClicked = {
            Log.d("FERI", "MyApp: primary when start $primaryColor")
            Log.d("FERI", "MyApp: state when start $colorState")
            colorState = ColorState.NORMAL
            if (state == TimerState.INIT) {
                initialTime = seconds
                millis = seconds * 1000L
            }

            state = TimerState.RUNNING
            job = scope.launch {
                var startMillis = System.currentTimeMillis()
                try {
                    while (millis > 0L) {
                        delay(10)
                        val tickMillis = System.currentTimeMillis()
                        millis -= tickMillis - startMillis
                        startMillis = tickMillis
                        seconds = ((millis + 1000) / 1000).toInt()
                    }
                    state = TimerState.DONE

                    while (true) {
                        colorState = if (colorState == ColorState.NORMAL) ColorState.INVERTED else ColorState.NORMAL
                        delay(500)
                    }
                } catch (e: CancellationException) {
                    val cancelMillis = System.currentTimeMillis()
                    millis -= cancelMillis - startMillis
                }
            }
        },
        onPauseClicked = {
            Log.d("FERI", "MyApp: primary when paused $primaryColor")
            Log.d("FERI", "MyApp: state when paused $colorState")
            job?.cancel()
            colorState = ColorState.INVERTED
            state = TimerState.PAUSED
            Log.d("FERI", "MyApp: state when exiting paused $colorState")
        },
        onResetClicked = {
            job?.cancel()
            seconds = initialTime
            colorState = ColorState.NORMAL
            state = TimerState.INIT
        }
    )
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
