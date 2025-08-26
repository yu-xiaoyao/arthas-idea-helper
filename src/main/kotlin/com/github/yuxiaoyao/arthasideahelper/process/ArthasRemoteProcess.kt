package com.github.yuxiaoyao.arthasideahelper.process

import com.google.common.net.HostAndPort
import com.intellij.remote.RemoteProcess
import java.io.InputStream
import java.io.OutputStream


/**
 * @author kerryzhang on 2025/08/26
 */

class ArthasRemoteProcess : RemoteProcess() {

    fun getId(): String {
        return ""
    }

    override fun killProcessTree(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isDisconnected(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLocalTunnel(remotePort: Int): HostAndPort? {
        return null
    }

    override fun setWindowSize(columns: Int, rows: Int) {
        TODO("Not yet implemented")
    }

    override fun getOutputStream(): OutputStream? {
        TODO("Not yet implemented")
    }

    override fun getInputStream(): InputStream? {
        TODO("Not yet implemented")
    }

    override fun getErrorStream(): InputStream? {
        TODO("Not yet implemented")
    }

    override fun waitFor(): Int {
        TODO("Not yet implemented")
    }

    override fun exitValue(): Int {
        TODO("Not yet implemented")
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }
}