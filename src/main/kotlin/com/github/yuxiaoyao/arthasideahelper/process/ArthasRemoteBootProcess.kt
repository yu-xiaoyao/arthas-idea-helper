package com.github.yuxiaoyao.arthasideahelper.process

import java.io.InputStream
import java.io.OutputStream


/**
 * @author Kerry2 on 2025/08/26
 */

class ArthasRemoteBootProcess : ArthasRemoteProcess() {
    override fun isDisconnected(): Boolean {
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