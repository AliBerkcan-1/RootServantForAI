package com.aerolon.rootservant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class BakingViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    private fun isPathSafe(path: String): Boolean {
        if (path.isBlank()) return false
        val regex = "^[a-zA-Z0-9_./-]+$".toRegex()
        return regex.matches(path) && !path.contains("..")
    }


    fun sendRootPrompt(apiKey: String, prompt: String) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val model = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = apiKey
                )


                val systemPrompt = """
    You are an AI assistant that generates terminal commands for a rooted Android device.
    
    You MUST respond ONLY in the following format:
    COMMAND: <linux command>
    
    Rules:
    - DO NOT write any explanations.
    - Output ONLY a single line.
    - You are strictly limited to using these commands: chmod, cp, rm, mount, mkdir
      
    Example:
    COMMAND: chmod 644 /system/build.prop
""".trimIndent()

                val fullPrompt = "$systemPrompt\nKullanıcı isteği: $prompt"

                val response = model.generateContent(fullPrompt)
                val text = response.text?.trim() ?: ""

                if (!text.startsWith("COMMAND:")) {
                    _uiState.value = UiState.Error("AI düzgün komut üretmedi:\n$text")
                    return@launch
                }

                val command = text.removePrefix("COMMAND:").trim()

                // 🔒 ekstra güvenlik
                if (!isCommandSafe(command)) {
                    _uiState.value = UiState.Error("Güvenlik: Tehlikeli komut engellendi")
                    return@launch
                }

                _uiState.value = UiState.Success("Çalıştırılıyor:\n$command")

                val partition = extractPartition(command)

                executeRootCommand("mount -o remount,rw $partition")

                val (success, output) = executeRootCommand("su -c 'mount -o remount,rw $partition'")

                if (success) {
                    _uiState.value = UiState.Success("✅ Başarılı:\n$output")
                } else {
                    _uiState.value = UiState.Error("❌ Hata:\n$output")
                }

            } catch (e: Exception) {
                _uiState.value = UiState.Error("Crash: ${e.localizedMessage}")
            }
        }
    }

    private fun extractPartition(path: String): String {
        return when {
            path.startsWith("/system") -> "/system"
            path.startsWith("/vendor") -> "/vendor"
            path.startsWith("/product") -> "/product"
            path.startsWith("/data") -> "/data"
            else -> "/"
        }
    }

    // 🔒 KOMUT GÜVENLİK KONTROLÜ
    private fun isCommandSafe(command: String): Boolean {
        val allowed = listOf("chmod", "cp", "rm", "mount", "mkdir")

        if (allowed.none { command.startsWith(it) }) return false

        if (command.contains(";") || command.contains("&&")) return false

        if (command.contains("/system") || command.contains("/data")) {
            executeRootCommand("mount -o remount,rw /system")
            executeRootCommand("mount -o remount,rw /")
        }

        val (success, output) = executeRootCommand(command)

        executeRootCommand("mount -o remount,ro /system")

        return true
    }

    // 💀 ROOT EXECUTOR
    private fun executeRootCommand(command: String): Pair<Boolean, String> {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))

            val output = StringBuilder()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))

            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            while (errorReader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            val exitCode = process.waitFor()

            Pair(exitCode == 0, output.toString().trim())

        } catch (e: Exception) {
            Pair(false, e.localizedMessage ?: "Root hata")
        }
    }
}