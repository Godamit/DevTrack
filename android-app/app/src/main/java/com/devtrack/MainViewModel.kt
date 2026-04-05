package com.devtrack

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var cpu = mutableStateOf("Loading...")
    var ram = mutableStateOf("Loading...")
    var disk = mutableStateOf("Loading...")
    val cpuHistory = mutableStateListOf<Float>()

    init {
        fetchAll()
    }

    fun fetchAll() {
        viewModelScope.launch {
            try {
                val cpuResponse = RetrofitClient.api.getCpu().cpu
                cpu.value = cpuResponse
                
                // Update history for the graph
                val numericValue = cpuResponse.replace("%", "").toFloatOrNull() ?: 0f
                cpuHistory.add(numericValue)
                if (cpuHistory.size > 20) {
                    cpuHistory.removeAt(0)
                }

                ram.value = RetrofitClient.api.getRam()["used"] ?: "N/A"
                disk.value = RetrofitClient.api.getDisk()["used"] ?: "N/A"
            } catch (e: Exception) {
                e.printStackTrace()
                cpu.value = "Error"
                ram.value = "Error"
                disk.value = "Error"
            }
        }
    }

}