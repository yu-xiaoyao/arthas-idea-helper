package com.github.yuxiaoyao.arthasideahelper.action

import com.github.yuxiaoyao.arthasideahelper.process.ArthasProcessManager
import com.github.yuxiaoyao.arthasideahelper.process.ArthasRemoteTelnetProcess
import com.github.yuxiaoyao.arthasideahelper.utils.TelnetUtils
import com.github.yuxiaoyao.arthasideahelper.utils.ToolWindowsUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger


/**
 * @author Kerry2 on 2025/08/26
 */

private val logger = Logger.getInstance(ArthasTestAction::class.java)

class ArthasTestAction : AnAction("Test Arthas") {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        if (project == null) {
            return
        }
        val telnetClient = TelnetUtils.createTelnetClient("127.0.0.1", 3658)
        if (telnetClient == null) {
            return
        }
        val tabId = "127.0.0.1:3658"
        val processHandler = ArthasProcessManager.createProcessHandler(tabId, ArthasRemoteTelnetProcess(telnetClient))
        ToolWindowsUtils.addArthasConsole(project, tabId, processHandler, forceAdd = true)
    }
}