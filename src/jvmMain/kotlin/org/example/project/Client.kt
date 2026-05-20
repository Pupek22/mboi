package org.example.project

import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.Socket

class Client(address: String = "127.0.0.1", port: Int = 8080) {
    private val socket = Socket(address, port)
    private val writer: BufferedWriter = socket.getOutputStream().bufferedWriter()
    private val reader: BufferedReader = socket.getInputStream().bufferedReader()

    @Synchronized
    fun sendMessage(message: Message) {
        writer.write(message.from)
        writer.newLine()
        writer.write(message.message)
        writer.newLine()
        writer.flush()
    }

    fun readMessage(): Message? {
        val from = reader.readLine() ?: return null
        val text = reader.readLine() ?: return null
        return Message(from, text)
    }

    fun close() {
        socket.close()
    }
}
