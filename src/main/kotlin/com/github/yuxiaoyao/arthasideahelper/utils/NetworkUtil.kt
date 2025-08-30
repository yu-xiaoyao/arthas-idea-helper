package com.github.yuxiaoyao.arthasideahelper.utils

import java.io.IOException


/**
 * @author kerryzhang on 2025/08/15
 */

object NetworkUtil {

    const val MAX_RETRY_ATTEMPTS = 50

    var httpPort = ArthasUtils.DEFAULT_HTTP_PORT
    var telnetPort = ArthasUtils.DEFAULT_TELNET_PORT

    fun getAvailableHttPort(): Int {
        repeat(MAX_RETRY_ATTEMPTS) {
            val port = checkPortAvailable(httpPort)
            if (port > 0) {
                return port
            }
            httpPort += 1
        }
        return -1
    }

    fun getAvailableTelnetPort(): Int {
        repeat(MAX_RETRY_ATTEMPTS) {
            val port = checkPortAvailable(telnetPort)
            if (port > 0) {
                return port
            }
            telnetPort += 1
        }
        return -1
    }


    fun checkPortAvailable(port: Int): Int {
        return try {
            java.net.ServerSocket(port).use { socket ->
//                socket.reuseAddress = true
                socket.localPort
            }
        } catch (_: IOException) {
            return -1
        }
    }
}