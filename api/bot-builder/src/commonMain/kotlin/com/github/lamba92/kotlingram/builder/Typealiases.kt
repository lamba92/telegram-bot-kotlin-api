package com.github.lamba92.kotlingram.builder

typealias InlineQueryHandler = suspend InlineQueryContext.() -> Unit
typealias SendMessageHandler = suspend MessageContext.() -> Unit

@Deprecated("Deprecated in favour of TelegramPollingBotBuilder", ReplaceWith("TelegramPollingBotBuilder"))
typealias TelegramLongPollingBotBuilder = TelegramPollingBotBuilder

@Deprecated("Deprecated in favour of `inlineQuery`", ReplaceWith("inlineQuery"))
val InlineQueryContext.query
    get() = inlineQuery
