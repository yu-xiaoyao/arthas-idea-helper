package com.github.yuxiaoyao.arthasideahelper.telnet

import com.intellij.remote.ColoredRemoteProcessHandler


/**
 * @author kerryzhang on 2025/08/26
 */

class ColoredTelnetProcessHandler(process: TelnetRemoteProcess) :
    ColoredRemoteProcessHandler<TelnetRemoteProcess>(process, "Telnet Session", Charsets.UTF_8) {


}