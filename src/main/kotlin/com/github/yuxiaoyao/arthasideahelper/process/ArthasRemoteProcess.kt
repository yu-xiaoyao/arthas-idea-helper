package com.github.yuxiaoyao.arthasideahelper.process

import com.google.common.net.HostAndPort
import com.intellij.remote.RemoteProcess


/**
 * @author kerryzhang on 2025/08/26
 */

abstract class ArthasRemoteProcess : RemoteProcess() {

    override fun killProcessTree(): Boolean {
        return true
    }

    override fun getLocalTunnel(remotePort: Int): HostAndPort? {
        return null
    }

    override fun setWindowSize(columns: Int, rows: Int) {
    }
}