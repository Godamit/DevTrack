package com.devtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkMode by rememberSaveable { mutableStateOf(systemTheme) }
            MaterialTheme(
                colorScheme = if (isDarkMode) darkColorScheme(
                    primary = Color(0xFF00FFFF),
                    background = Color(0xFF121212),
                    surface = Color(0xFF1E1E1E),
                    onSurface = Color.White,
                    onBackground = Color.White
                ) else lightColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DashboardScreen(isDarkMode, onThemeToggle = { isDarkMode = it })
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    vm: MainViewModel = viewModel()
) {
    val cpu = vm.cpu.value
    val ram = vm.ram.value
    val disk = vm.disk.value

    LaunchedEffect(Unit) {
        while (true) {
            vm.fetchAll()
            delay(5000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DevTrack",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Dark Mode",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(checked = isDarkMode, onCheckedChange = onThemeToggle)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val cpuValue = cpu.replace("%", "").toFloatOrNull() ?: 0f
        if (cpuValue > 80f) {
            Text(
                text = "⚠️ High CPU usage!",
                color = Color.Red,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        CpuChart(vm.cpuHistory, isDarkMode)

        Spacer(modifier = Modifier.height(20.dp))

        UsageCard(title = "CPU Usage", value = cpu, isDarkMode = isDarkMode)
        Spacer(modifier = Modifier.height(12.dp))
        UsageCard(title = "RAM Usage", value = ram, isDarkMode = isDarkMode)
        Spacer(modifier = Modifier.height(12.dp))
        UsageCard(title = "Disk Usage", value = disk, isDarkMode = isDarkMode)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { vm.fetchAll() },
            modifier = Modifier.fillMaxWidth(),
            colors = if (isDarkMode) ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00FFFF),
                contentColor = Color.Black
            ) else ButtonDefaults.buttonColors()
        ) {
            Text("Refresh Now")
        }
    }
}

@Composable
fun UsageCard(title: String, value: String, isDarkMode: Boolean) {
    var percentage = 0f
    try {
        if (value.contains("/")) {
            val parts = value.replace("MB", "").replace("GB", "").split("/")
            percentage = (parts[0].trim().toFloat() / parts[1].trim().toFloat()) * 100
        } else {
            percentage = value.replace("%", "").trim().toFloatOrNull() ?: 0f
        }
    } catch (e: Exception) {
        percentage = 0f
    }
    
    val baseContentColor = if (isDarkMode) Color.White else Color.Black
    val color = when {
        value == "Error" -> Color.Red
        value == "Loading..." -> Color.Gray
        percentage < 50 -> baseContentColor
        percentage < 80 -> Color(0xFFFFA500) // Orange
        else -> Color.Red
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title, 
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = color,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun CpuChart(history: List<Float>, isDarkMode: Boolean) {
    val neonColor = if (isDarkMode) 0xFF00FFFF.toInt() else android.graphics.Color.BLUE
    val gridColor = if (isDarkMode) 0xFF333333.toInt() else android.graphics.Color.LTGRAY
    val textColor = if (isDarkMode) android.graphics.Color.WHITE else android.graphics.Color.BLACK

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawLabels(false)
                    setDrawAxisLine(true)
                    axisLineColor = gridColor
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    this.gridColor = gridColor
                    this.textColor = textColor
                    axisMinimum = 0f
                    spaceTop = 20f 
                    setDrawAxisLine(true)
                    axisLineColor = gridColor
                }
                
                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        },
        update = { chart ->
            val entries = history.mapIndexed { i, v ->
                Entry(i.toFloat(), v)
            }

            val dataSet = LineDataSet(entries, "CPU %").apply {
                color = neonColor
                mode = LineDataSet.Mode.CUBIC_BEZIER
                lineWidth = 3f
                setDrawValues(false)
                setDrawCircles(false)
                
                setDrawFilled(true)
                fillColor = neonColor
                fillAlpha = if (isDarkMode) 60 else 30
            }

            chart.data = LineData(dataSet)
            chart.axisLeft.textColor = textColor
            chart.invalidate()
        }
    )
}
