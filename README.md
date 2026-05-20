# mboi

Simple Kotlin Multiplatform Compose Desktop chat with TCP client/server.

## Run server

```bash
./gradlew runServer
```

Server listens on `127.0.0.1:8080`, stores message history in memory, broadcasts new messages to all clients, and sends history to newly connected clients.

## Run client

```bash
./gradlew run
```

In the client:
1. Enter username on Login screen.
2. Click **Login** (connects to `127.0.0.1:8080`).
3. Send messages from Chat screen.
