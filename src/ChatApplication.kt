package chat

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.content.*
import io.ktor.response.header
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.*
import java.time.*

/**
 * Entry Point of the application. This function is referenced in the
 * resources/application.conf file inside the ktor.application.modules.
 *
 * Notice that the fqname of this function is io.ktor.samples.chat.ChatApplicationKt.main
 * For top level functions, the class name containing the method in the JVM is FileNameKt.
 *
 * The `Application.main` part is Kotlin idiomatic that specifies that the main method is
 * an extension of the [Application] class, and thus can be accessed like a normal member `myapplication.main()`.
 */
@ExperimentalCoroutinesApi
@kotlinx.coroutines.ObsoleteCoroutinesApi
@io.ktor.util.KtorExperimentalAPI
fun Application.main() {
    ChatApplication().apply { main() }
}

/**
 * In this case we have a class holding our application state so it is not global and can be tested easier.
 */
@ExperimentalCoroutinesApi
@kotlinx.coroutines.ObsoleteCoroutinesApi
@io.ktor.util.KtorExperimentalAPI
class ChatApplication {
    /**
     * This class handles the logic of a [ChatServer2].
     * With the standard handlers [ChatServer2.memberJoin] or [ChatServer2.memberLeft] and operations like
     * sending messages to everyone or to specific people connected to the server.
     */
    private val server2 = ChatServer2()

    /**
     * This is the main method of the application in this class.
     */
    fun Application.main() {
        /**
         * First we install the features we need. They are bound to the whole application.
         * Since this method has an implicit [Application] receiver that supports the [install] method.
         */
        // This adds automatically Date and Server headers to each response, and would allow you to configure
        // additional headers served to each response.
        install(DefaultHeaders)
        // This uses use the logger to log every call (request/response)
        install(CallLogging)
        // This installs the websockets feature to be able to establish a bidirectional configuration
        // between the server and the client
        install(WebSockets) {
            pingPeriod = Duration.ofMinutes(1)
        }
        // This enables the use of sessions to keep information between requests/refreshes of the browser.
        install(Sessions) {
            cookie<ChatSession>("SESSION")
        }

        // This adds an interceptor that will create a specific session in each request if no session is available already.
        intercept(ApplicationCallPipeline.Features) {
            if (call.sessions.get<ChatSession>() == null) {
                call.sessions.set(ChatSession(generateNonce()))
            }
        }

        /**
         * Now we are going to define routes to handle specific methods + URLs for this application.
         */
        routing {

            /**
             * We're saying that an agency POSTs to this endpoint to send the models a notice that
             * the job is available.
             */
            post("/send") {
                server2.broadcastOpenJob("TheLimited")
                val response = call.response
                response.header("Access-Control-Allow-Origin", "*")
                call.respondText("Hey!")
            }

            webSocket("/ws2") {
                val session = call.sessions.get<ChatSession>()
                if (session == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                    return@webSocket
                }

                val member = session.id
                server2.memberJoin(member, this)
                server2.sendCompanies(member, this)

                // We starts receiving messages (frames).
                // Since this is a coroutine. This coroutine is suspended until receiving frames.
                // Once the connection is closed, this consumeEach will finish and the code will continue.
                incoming.consumeEach { frame ->
                    // Frames can be [Text], [Binary], [Ping], [Pong], [Close].
                    // We are only interested in textual messages, so we filter it.
                    if (frame is Frame.Text) {
                        // Now it is time to process the text sent from the user.
                        // At this point we have context about this connection, the session, the text and the server.
                        // So we have everything we need.
                        send(Frame.Text("pong ${frame.readText()}"))
                    }
                }
            }

            // This defines a block of static resources for the '/' path (since no path is specified and we start at '/')
            static {
                // This marks index.html from the 'web' folder in resources as the default file to serve.
                defaultResource("index.html", "web/commands")
                // This serves files from the 'web' folder in the application resources.
                resources("web")
            }

        }
    }

    /**
     * A chat session is identified by a unique nonce ID. This nonce comes from a secure random source.
     */
    data class ChatSession(val id: String)
}
