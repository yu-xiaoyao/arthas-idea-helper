package com.github.yuxiaoyao.arthasideahelper.action

import com.github.yuxiaoyao.arthasideahelper.ARTHAS_CONSOLE_TOOL_WINDOW_ID
import com.github.yuxiaoyao.arthasideahelper.telnet.ColoredTelnetProcessHandler
import com.github.yuxiaoyao.arthasideahelper.telnet.TelnetRemoteProcess
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import org.apache.commons.net.telnet.TelnetClient


/**
 * @author kerryzhang on 2025/08/24
 */

class TelnetColoredConsoleAction : AnAction("Open Telnet Colored Console") {


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project == null) return

//        val cmd = GeneralCommandLine("telnet", "127.0.0.1", "3658");

        try {
            // 建立 Telnet 连接
            val telnetClient = TelnetClient()
            telnetClient.connect("127.0.0.1", 3658)
            val telnetProcess = TelnetRemoteProcess(telnetClient)
            val processHandler = ColoredTelnetProcessHandler(telnetProcess)

            // 创建 ConsoleView
            val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
            consoleView.attachToProcess(processHandler)

            // 动态添加
            // 放到 ToolWindow 里展示
            val toolWindowManager = ToolWindowManager.getInstance(project)
            var toolWindow = toolWindowManager.getToolWindow(ARTHAS_CONSOLE_TOOL_WINDOW_ID)
            if (toolWindow == null) {
                toolWindow = toolWindowManager.registerToolWindow(ARTHAS_CONSOLE_TOOL_WINDOW_ID) {
                    icon = AllIcons.Debugger.Console
                }
                toolWindow.setAnchor(ToolWindowAnchor.BOTTOM, null);

                val content = toolWindow.contentManager.factory
                    .createContent(consoleView.component, "Telnet Session", false)
                toolWindow.contentManager.addContent(content)
                toolWindow.activate(null)

                // 启动数据转发
                processHandler.startNotify()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}