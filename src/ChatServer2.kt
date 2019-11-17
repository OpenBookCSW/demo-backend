package chat

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.*

@ExperimentalCoroutinesApi
class ChatServer2 {
    suspend fun sendCompanies(
        member: String,
        socket: WebSocketSession
    ) {
        GlobalScope.launch {
            while (socket.isActive) {
                socket.send(Frame.Text("Company2 ${LocalDateTime.now()}"))
                delay(5000)
            }
            println("Closed for member $member")
        }
    }
}
