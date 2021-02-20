package com.github.lamba92.kotlingram.examples.mingwx64

import com.github.lamba92.kotlingram.api.generated.InlineQueryResultPhoto
import com.github.lamba92.kotlingram.builder.buildPollingBot
import com.github.lamba92.kotlingram.builder.respond
import com.github.lamba92.kotlingram.builder.respondPhoto
import com.github.lamba92.kotlingram.builder.respondText
import io.ktor.client.engine.curl.*
import io.ktor.client.features.logging.*
import kotlinx.cinterop.toKString
import kotlinx.coroutines.runBlocking
import platform.posix.getenv

fun main(): Unit = runBlocking {
    val customMessage = ""
    val media = "https://www.tc-web.it/wp-content/uploads/2019/01/java.jpg"
    buildPollingBot {

        options {
            botApiToken = getenv("jvmTestBotToken")?.toKString()
            botUsername = "KotlingramJvmTestBot"

            engine(Curl) {
                install(Logging) {
                    level = LogLevel.ALL
                }
            }
        }

        handlers {
            messages {
                respondPhoto(
                    photo = media,
                    caption = "Hi, i'm Kotlingram JVM test bot",
                    replyToMessageId = message.messageId
                )
                respondText("You wrote to me \"${message.text}\", my message is $customMessage")
            }
            inlineQueries {

                val responses = buildList {
                    repeat(10) { index ->
                        add(
                            InlineQueryResultPhoto(
                                id = "response#$index",
                                title = "Inline response #$index",
                                type = "photo",
                                photoUrl = media,
                                thumbUrl = media
                            )
                        )
                    }
                }
                respond(responses)
            }
        }
    }
}
