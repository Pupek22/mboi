package org.example.project

import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.Collections
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread

class Server(private val port: Int = 8080) {
    private val history = CopyOnWriteArrayList<Message>()
    private val clients = Collections.synchronizedList(mutableListOf<ClientConnection>())

    fun start() {
        ServerSocket(port).use { serverSocket ->
            println("Chat server started on 127.0.0.1:$port")
            while (true) {
                val socket = serverSocket.accept()
                val connection = ClientConnection(socket)
                clients.add(connection)
                sendHistory(connection)
                thread(isDaemon = true) {
                    handleClient(connection)
                }
            }
        }
    }

    private fun sendHistory(connection: ClientConnection) {
        for (message in history) {
            connection.send(message)
        }
    }

    private fun handleClient(connection: ClientConnection) {
        try {
            while (true) {
                val from = connection.reader.readLine() ?: break
                val text = connection.reader.readLine() ?: break
                val msg = Message(from, text)
                history.add(msg)
                broadcast(msg)
            }
        } finally {
            connection.close()
            clients.remove(connection)
        }
    }

    private fun broadcast(message: Message) {
        val toRemove = mutableListOf<ClientConnection>()
        synchronized(clients) {
            for (client in clients) {
                try {
                    client.send(message)
                } catch (_: Exception) {
                    toRemove.add(client)
                }
            }
            if (toRemove.isNotEmpty()) {
                clients.removeAll(toRemove)
            }
        }
    }

    private class ClientConnection(socket: Socket) {
        private val socketRef = socket
        val reader: BufferedReader = socket.getInputStream().bufferedReader()
        private val writer: BufferedWriter = socket.getOutputStream().bufferedWriter()

        @Synchronized
        fun send(message: Message) {
            writer.write(message.from)
            writer.newLine()
            writer.write(message.message)
            writer.newLine()
            writer.flush()
        }

        fun close() {
            socketRef.close()
        }
    }
}

fun main() {
    Server(8080).start()
}
