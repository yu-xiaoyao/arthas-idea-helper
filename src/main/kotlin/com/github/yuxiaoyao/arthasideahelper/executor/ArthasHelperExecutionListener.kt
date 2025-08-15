package com.github.yuxiaoyao.arthasideahelper.executor

import com.github.yuxiaoyao.arthasideahelper.NOTIFICATION_ID
import com.github.yuxiaoyao.arthasideahelper.settings.ArthasHelperProjectSettings
import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.notification.NotificationGroupManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project


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
            val projectSettings = ArthasHelperProjectSettings.getInstance(env.project)
            showMyNotification(env.project)
        }
        super.processStarted(executorId, env, handler)
    }

    fun showMyNotification(project: Project?) {
        // 1. 获取或创建 NotificationGroup
        val notificationGroup = NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_ID)  // 在 plugin.xml 中声明


    }

}