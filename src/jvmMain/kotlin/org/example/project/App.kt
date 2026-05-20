package org.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

@Composable
fun App() {
    var username by remember { mutableStateOf("") }
    var loginInput by remember { mutableStateOf("John Doe") }
    val messages = remember { mutableStateListOf<Message>() }
    var outgoingText by remember { mutableStateOf("") }
    var client by remember { mutableStateOf<Client?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    MaterialTheme {
        if (username.isBlank()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Login")
                TextField(
                    value = loginInput,
                    onValueChange = { loginInput = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                Button(
                    onClick = {
                        val trimmed = loginInput.trim()
                        if (trimmed.isNotEmpty()) {
                            try {
                                client = Client("127.0.0.1", 8080)
                                username = trimmed
                                errorText = null
                            } catch (e: Exception) {
                                errorText = "Cannot connect to server: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Login")
                }
                errorText?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
            }
        } else {
            LaunchedEffect(client) {
                val activeClient = client ?: return@LaunchedEffect
                withContext(Dispatchers.IO) {
                    while (isActive) {
                        val message = activeClient.readMessage() ?: break
                        withContext(Dispatchers.Main) {
                            messages.add(message)
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Chat as $username")
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    items(messages) { msg ->
                        Text("${msg.from}: ${msg.message}")
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = outgoingText,
                        onValueChange = { outgoingText = it },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            val text = outgoingText.trim()
                            if (text.isNotEmpty()) {
                                client?.sendMessage(Message(username, text))
                                outgoingText = ""
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Send")
                    }
                }
            }
        }
    }
}
