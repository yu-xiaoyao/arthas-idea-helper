package com.github.yuxiaoyao.arthasideahelper.process

import com.intellij.remote.ColoredRemoteProcessHandler

/**
 * @author kerryzhang on 2025/08/26
 */

class ArthasColoredRemoteProcessHandler(process: ArthasRemoteProcess) :
    ColoredRemoteProcessHandler<ArthasRemoteProcess>(process, "Arthas", Charsets.UTF_8) {


//    override fun destroyProcessImpl() {
//        process.destroy()
//    }
//
//    override fun detachProcessImpl() {
//        notifyProcessDetached()
//    }
//
//    override fun detachIsDefault(): Boolean {
//        return false
//    }
}



