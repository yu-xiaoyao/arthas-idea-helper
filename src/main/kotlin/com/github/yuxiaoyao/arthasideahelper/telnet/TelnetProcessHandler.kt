package com.github.yuxiaoyao.arthasideahelper.telnet

import com.intellij.execution.process.AnsiEscapeDecoder
import com.intellij.execution.process.BaseOSProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.Key
import java.nio.charset.StandardCharsets


/**
 * @author kerryzhang on 2025/08/24
 */


private val logger = Logger.getInstance(TelnetProcessHandler::class.java)

class TelnetProcessHandler(process: RemoteTelnetProcess) :
    BaseOSProcessHandler(process, "Telnet Session", StandardCharsets.UTF_8) {

    private val ansiEscapeDecoder = AnsiEscapeDecoder()


    override fun notifyTextAvailable(text: String, outputType: Key<*>) {

//        logger.info("notifyTextAvailable. $text ${outputType.javaClass} - $outputType")

        ansiEscapeDecoder.escapeText(text, outputType) { text, attributes ->

            logger.info("escapeText. attributes: ${attributes.javaClass} - $attributes - ${attributes.hashCode()}")

            val key = TextAttributesKey.createTextAttributesKey(
                "ANSI_COLOR_${attributes.hashCode()}",
            )
            val type = ConsoleViewContentType("ansi", key);

            super.notifyTextAvailable(
                text,
                outputType
            )
        }
    }

    override fun destroyProcessImpl() {
        process.destroy()
    }

    override fun detachProcessImpl() {
        notifyProcessDetached()
    }

    override fun detachIsDefault(): Boolean {
        return false
    }

}