package com.github.yuxiaoyao.arthasideahelper.utils

import org.jetbrains.jps.util.SystemInfo


/**
 * @author Kerry2 on 2025/08/27
 */

object SocketUtils {


    fun findTcpListenProcess(port: Int): Long {

        if (SystemInfo.isWindows) {
            val command = arrayOf("netstat", "-ano", "-p", "TCP")
            val lines = ExecutingCommand.runNative(command)
            for (line in lines) {
                if (line.contains("LISTENING")) {
                    // TCP 0.0.0.0:49168 0.0.0.0:0 LISTENING 476
                    val strings: Array<String> =
                        line.trim { it <= ' ' }.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (strings.size == 5) {
                        if (strings[1].endsWith(":$port")) {
                            return strings[4].toLong()
                        }
                    }
                }
            }
        }

        //TODO more os

        return -1L
    }


}