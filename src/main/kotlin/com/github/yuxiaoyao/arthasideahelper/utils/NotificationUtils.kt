package com.github.yuxiaoyao.arthasideahelper.utils

import com.github.yuxiaoyao.arthasideahelper.ARTHAS_NOTIFICATION_ID
import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.PLUGIN_NOTIFICATION_ID
import com.github.yuxiaoyao.arthasideahelper.settings.ArthasHelperProjectSettings
import com.intellij.ide.BrowserUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType


/**
 * @author kerryzhang on 2025/08/16
 */

object NotificationUtils {

    /**
     * 发送通知(会自动隐藏)
     */
    fun sendNotification(
        project: Project,
        content: String,
        title: String? = null,
        messageType: MessageType = MessageType.INFO
    ) {
        val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup(PLUGIN_NOTIFICATION_ID)
        val notification = notificationGroup.createNotification(content, messageType)
        if (title != null) {
            notification.setTitle(title)
        } else {
            notification.setTitle(MyBundle.message("running.notification.title"))
        }
        notification.notify(project)
    }


    private var lastNotification: Notification? = null

    fun showArthasRunningNotification(
        project: Project
    ) {

        if (lastNotification != null) {
            if (!lastNotification!!.isExpired) {
                lastNotification?.expire()
            }
        }

        val projectSettings = ArthasHelperProjectSettings.getInstance(project)
        if (!ArthasUtils.isProjectHasArthasAgent(projectSettings)) {
            return
        }

        val httpUrl = "http://${ArthasUtils.LOCAL_IP}:${projectSettings.httpPort}"
        val telnetCommand = "telnet ${ArthasUtils.LOCAL_IP} ${projectSettings.telnetPort}"

        val content = "http: $httpUrl. \n\n telnet: $telnetCommand"

        val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup(ARTHAS_NOTIFICATION_ID)

        val notification = notificationGroup.createNotification(content, MessageType.INFO)
        notification.setTitle(MyBundle.message("running.notification.title"))

        if (projectSettings.telnetPort != -1) {
            notification.addAction(NotificationAction.createSimple(MyBundle.message("open.telnetTerminal")) {
                ArthasTerminalUtils.openTerminalAndRunCommand(project, telnetCommand)
                notification.expire()
                lastNotification = null
            })
        }

        if (projectSettings.httpPort != -1) {
            notification.addAction(NotificationAction.createSimple(MyBundle.message("open.httpBrowser")) {
                BrowserUtil.open(httpUrl)
                notification.expire()
                lastNotification = null
            })
        }

        notification.notify(project)
        lastNotification = notification
    }
}