package com.github.yuxiaoyao.arthasideahelper.process

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener

/**
 * @author kerryzhang on 2025/08/26
 */

object ArthasProcessManager {


    fun createProcessHandler(process: ArthasRemoteProcess): ArthasColoredRemoteProcessHandler {
        val processHandler = ArthasColoredRemoteProcessHandler(process)

        processHandler.addProcessListener(object : ProcessListener {
            override fun processTerminated(event: ProcessEvent) {
            }
        })

        return processHandler
    }

}