package com.github.yuxiaoyao.arthasideahelper.action

import com.github.yuxiaoyao.arthasideahelper.telnet.TelnetProcessHandler
import com.github.yuxiaoyao.arthasideahelper.telnet.TelnetRemoteProcess
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import org.apache.commons.net.telnet.TelnetClient


/**
 * @author kerryzhang on 2025/08/25
 */

class SendTelnetCommandAction : AnAction("SendTelnetCommandAction") {

    private var processHandler: TelnetProcessHandler? = null


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project == null) return

        if (processHandler == null) {
            try {
                // 建立 Telnet 连接
                val telnetClient = TelnetClient()
                telnetClient.connect("127.0.0.1", 3658)
                val telnetProcess = TelnetRemoteProcess(telnetClient)
                processHandler = TelnetProcessHandler(telnetProcess)

                // 创建 ConsoleView
                val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
                consoleView.attachToProcess(processHandler!!)

                // 放到 ToolWindow 里展示
                var toolWindow = ToolWindowManager.getInstance(project).getToolWindow("TelnetConsole")
                if (toolWindow == null) {

                    // RegisterToolWindowTask.closable()


                    toolWindow = ToolWindowManager.getInstance(project)
                        .registerToolWindow("TelnetConsole", {

                        })
                    val content = toolWindow.contentManager.factory
                        .createContent(consoleView.component, "Telnet Session", false)
                    toolWindow.contentManager.addContent(content)
                    toolWindow.activate(null)

                    // 启动数据转发
                    processHandler!!.startNotify()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else {
            processHandler!!.processInput.write("thread\n".toByteArray())
            processHandler!!.processInput.flush()
        }
    }
}