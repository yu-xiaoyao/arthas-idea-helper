package com.github.yuxiaoyao.arthasideahelper.action

import com.github.yuxiaoyao.arthasideahelper.telnet.RemoteTelnetProcess
import com.github.yuxiaoyao.arthasideahelper.telnet.TelnetProcessHandler
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import org.apache.commons.net.telnet.TelnetClient


/**
 * @author kerryzhang on 2025/08/24
 */

class OpenTelnetConsoleAction : AnAction("Open Telnet Console") {


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project == null) return

        try {
            // 建立 Telnet 连接
            val telnetClient = TelnetClient()
            telnetClient.connect("127.0.0.1", 3658)
            val telnetProcess = RemoteTelnetProcess(telnetClient)
            val processHandler = TelnetProcessHandler(telnetProcess)

            // 创建 ConsoleView
            val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole()
            consoleView.attachToProcess(processHandler)

            // 放到 ToolWindow 里展示
            var toolWindow = ToolWindowManager.getInstance(project)
                .getToolWindow("TelnetConsole")
            if (toolWindow == null) {
                toolWindow = ToolWindowManager.getInstance(project)
                    .registerToolWindow("TelnetConsole", {

                    })
                val content = toolWindow.getContentManager().getFactory()
                    .createContent(consoleView.getComponent(), "Telnet Session", false)
                toolWindow.getContentManager().addContent(content)
                toolWindow.activate(null)

                // 启动数据转发
                processHandler.startNotify()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}