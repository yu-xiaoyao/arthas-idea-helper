package com.github.yuxiaoyao.arthasideahelper.action

import com.github.yuxiaoyao.arthasideahelper.telnet.TelnetProcessHandler
import com.github.yuxiaoyao.arthasideahelper.telnet.TelnetRemoteProcess
import com.github.yuxiaoyao.arthasideahelper.utils.TelnetUtils
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.wm.ToolWindowManager


/**
 * @author kerryzhang on 2025/08/24
 */

private val logger = Logger.getInstance(OpenTelnetConsoleAction::class.java)

class OpenTelnetConsoleAction : AnAction("Open Telnet Console") {


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project == null) return


        val telnetClient = TelnetUtils.createTelnetClient("127.0.0.1", 36581)
        if (telnetClient == null) {
            return
        }

        try {
            val telnetProcess = TelnetRemoteProcess(telnetClient)
            val processHandler = TelnetProcessHandler(telnetProcess)

            // 创建 ConsoleView
            val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
            consoleView.attachToProcess(processHandler)

            // 放到 ToolWindow 里展示
            var toolWindow = ToolWindowManager.getInstance(project).getToolWindow("TelnetConsole")
            if (toolWindow == null) {
                toolWindow = ToolWindowManager.getInstance(project)
                    .registerToolWindow("TelnetConsole", {

                    })
                val content = toolWindow.contentManager.factory
                    .createContent(consoleView.component, "Telnet Session", false)
                toolWindow.contentManager.addContent(content)
                toolWindow.activate(null)

                // 启动数据转发
                processHandler.startNotify()
            }
        } catch (ex: Exception) {
            logger.error("Error opening Telnet console = ${ex.message}", ex)
        }
    }
}