package com.github.yuxiaoyao.arthasideahelper.executor

import com.github.yuxiaoyao.arthasideahelper.utils.ArthasRunningNotification
import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.diagnostic.Logger


/**
 * @author Kerry2 on 2025/08/16
 */

private val logger = Logger.getInstance(ArthasHelperExecutionListener::class.java)

class ArthasHelperExecutionListener : ExecutionListener {

    override fun processStarted(
        executorId: String,
        env: ExecutionEnvironment,
        handler: ProcessHandler
    ) {
        logger.info("Arthas Helper execution started. executorId = $executorId")
        if (executorId == ArthasExecutor.EXECUTOR_ID) {
//            val projectSettings = ArthasHelperProjectSettings.getInstance(env.project)
            ArthasRunningNotification.showArthasRunningNotification(env.project)
        }
        super.processStarted(executorId, env, handler)
    }


}