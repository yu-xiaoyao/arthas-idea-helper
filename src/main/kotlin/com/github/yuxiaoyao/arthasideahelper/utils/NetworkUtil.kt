package com.github.yuxiaoyao.arthasideahelper.utils

import java.io.IOException


/**
 * @author kerryzhang on 2025/08/15
 */

object NetworkUtil {


    fun findAvailablePort(): Int {
        return try {
            java.net.ServerSocket(0).use { socket ->
//                socket.reuseAddress = true
                socket.localPort
            }
        } catch (_: IOException) {
            return 0
        }
    }
}