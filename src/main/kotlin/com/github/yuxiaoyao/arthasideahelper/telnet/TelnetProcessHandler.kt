package com.github.yuxiaoyao.arthasideahelper.telnet

import com.intellij.execution.process.BaseOSProcessHandler
import java.nio.charset.StandardCharsets


/**
 * @author kerryzhang on 2025/08/24
 */

class TelnetProcessHandler(process: RemoteTelnetProcess) :
    BaseOSProcessHandler(process, "Telnet Session", StandardCharsets.UTF_8) {

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