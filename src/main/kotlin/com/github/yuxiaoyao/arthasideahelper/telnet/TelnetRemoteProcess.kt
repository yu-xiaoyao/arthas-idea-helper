package com.github.yuxiaoyao.arthasideahelper.telnet

import com.google.common.net.HostAndPort
import com.intellij.remote.RemoteProcess
import kotlinx.html.InputAutoComplete.username
import org.apache.commons.net.telnet.TelnetClient
import java.io.*
import java.nio.charset.StandardCharsets


/**
 * @author kerryzhang on 2025/08/24
 */

class TelnetRemoteProcess(
      val telnetClient: TelnetClient
) : RemoteProcess() {

    init {
        // 自动登录
//        login("arthas");
    }

    override fun killProcessTree(): Boolean {
        // killProcessTree() 会在远端执行类似 kill -9 <pid> 并递归到所有子进程，保证干净地清理资源。
        return true
    }

    override fun isDisconnected(): Boolean {
        return !telnetClient.isConnected
    }

    override fun getLocalTunnel(p0: Int): HostAndPort? {
        return null
    }

    override fun setWindowSize(p0: Int, p1: Int) {
    }

    override fun getOutputStream(): OutputStream? {
        return telnetClient.outputStream
    }

    override fun getInputStream(): InputStream? {
        return telnetClient.inputStream
    }

    override fun getErrorStream(): InputStream? {
        // Telnet 没有 stderr
        return ByteArrayInputStream(ByteArray(0))
    }

    override fun waitFor(): Int {
        while (telnetClient.isConnected) {
            Thread.sleep(200)
        }
        return 0
    }

    override fun exitValue(): Int {
        if (telnetClient.isConnected) {
            throw IllegalThreadStateException("Still running")
        }
        return 0
    }

    override fun destroy() {
        telnetClient.disconnect()
    }


    @Throws(IOException::class)
    private fun login(password: String) {
        val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        val writer: PrintWriter = PrintWriter(OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)

        val buf = CharArray(1024)
        val sb = StringBuilder()

        while (true) {
            if (reader.ready()) {
                val len = reader.read(buf)
                if (len > 0) {
                    sb.append(buf, 0, len)
                    val text = sb.toString()

                    if (text.contains("login:")) {
                        writer.println(username)
                        sb.setLength(0)
                    } else if (text.contains("Password:")) {
                        writer.println(password)
                        sb.setLength(0)
                        break // 登录结束
                    }
                }
            } else {
                try {
                    Thread.sleep(100)
                } catch (ignored: InterruptedException) {
                }
            }
        }
    }


}