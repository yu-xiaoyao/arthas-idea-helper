package com.github.yuxiaoyao.arthasideahelper.process

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.diagnostic.Logger
import fleet.util.hashSetMultiMap

/**
 * @author kerryzhang on 2025/08/26
 */


private val logger = Logger.getInstance(ArthasProcessManager::class.java)

object ArthasProcessManager {

    private val processMap = hashSetMultiMap<String, ProcessHandler>()

    fun createProcessHandler(id: String, process: ArthasRemoteProcess): ArthasColoredRemoteProcessHandler {
        val processHandler = ArthasColoredRemoteProcessHandler(process)
        processHandler.addProcessListener(object : ProcessListener {
            override fun processTerminated(event: ProcessEvent) {
                logger.error("Process terminated" + event.exitCode)
            }
        })
        processMap.put(id, processHandler)
        return processHandler
    }

}