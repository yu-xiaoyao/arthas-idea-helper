package com.github.yuxiaoyao.arthasideahelper.process

import org.apache.commons.net.telnet.TelnetClient
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.OutputStream


/**
 * @author Kerry2 on 2025/08/26
 */

class ArthasRemoteTelnetProcess(val telnetClient: TelnetClient) : ArthasRemoteProcess() {

    override fun isDisconnected(): Boolean {
        return !telnetClient.isConnected
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
}