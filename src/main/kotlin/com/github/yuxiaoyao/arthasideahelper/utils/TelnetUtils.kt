package com.github.yuxiaoyao.arthasideahelper.utils

import com.intellij.openapi.diagnostic.Logger
import org.apache.commons.net.telnet.TelnetClient


/**
 * @author Kerry2 on 2025/08/26
 */

private val logger = Logger.getInstance(TelnetUtils::class.java)

object TelnetUtils {

    fun createTelnetClient(
        host: String, port: Int,
        connectTimeout: Int = 2000
    ): TelnetClient? {

        logger.info("createTelnetClient host = $host, port = $port, connectTimeout = $connectTimeout")

        val telnetClient = TelnetClient()
        telnetClient.connectTimeout = connectTimeout
        try {
            telnetClient.connect(host, port)
            return telnetClient
        } catch (ex: Exception) {
            logger.error("Telnet Arthas on $host:$port Error opening Telnet console = ${ex.message}", ex)
        }
        return null
    }

}