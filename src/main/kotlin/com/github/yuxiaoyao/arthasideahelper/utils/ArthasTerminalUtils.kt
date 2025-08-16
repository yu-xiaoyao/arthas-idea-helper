package com.github.yuxiaoyao.arthasideahelper.utils


import com.intellij.openapi.project.Project
import org.jetbrains.plugins.terminal.TerminalToolWindowManager


/**
 * @author kerryzhang on 2025/08/16
 */

object ArthasTerminalUtils {

    fun openTerminalAndRunCommand(project: Project, command: String) {
        val toolWindowManager = TerminalToolWindowManager.getInstance(project)
        val createNewSession = toolWindowManager.createNewSession()
        createNewSession.sendCommandToExecute(command)


//        val terminalWindow = ToolWindowManager.getInstance(project).getToolWindow("Terminal")
//        if (terminalWindow != null) {
//            terminalWindow.activate {
//                executeCommand(project, "telnet 127.0.0.1 3333")
//            }
//        }
    }

    private fun executeCommand(project: Project, command: String) {
        ProcessBuilder(command).start()
    }

}