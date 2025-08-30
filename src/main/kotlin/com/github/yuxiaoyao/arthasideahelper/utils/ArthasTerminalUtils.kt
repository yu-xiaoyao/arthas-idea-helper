package com.github.yuxiaoyao.arthasideahelper.utils


import com.github.yuxiaoyao.arthasideahelper.settings.ArthasHelperSettings
import com.github.yuxiaoyao.arthasideahelper.settings.ArthasParameterState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.OSProcessHandler
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import org.jetbrains.plugins.terminal.TerminalToolWindowManager
import org.jetbrains.plugins.terminal.runner.LocalTerminalStartCommandBuilder


/**
 * @author kerryzhang on 2025/08/16
 */

private val logger = Logger.getInstance(ArthasTerminalUtils::class.java)

object ArthasTerminalUtils {

    fun openConsoleByPid(
        project: Project,
        javaExecutable: String,
        pid: Any,
        state: ArthasParameterState
    ): Boolean {

        logger.info("openConsoleByPid. javaExecutable = $javaExecutable, pid = $pid")

        val arthasBootPath = ArthasHelperSettings.getInstance().arthasBootPath
        if (arthasBootPath.isEmpty()) {
            NotificationUtils.sendNotification(
                project,
                "Cannot find arthas boot path",
                messageType = MessageType.WARNING
            )
            return false
        }
        if (state.telnetEnable) {
            if (state.telnetPort != NetworkUtil.checkPortAvailable(state.telnetPort)) {
                NotificationUtils.sendNotification(
                    project,
                    "Current Telnet port Unavailable ${state.telnetPort}",
                    messageType = MessageType.WARNING
                )
                return false
            }
        }
        if (state.httpEnable) {
            if (state.httpPort != NetworkUtil.checkPortAvailable(state.httpPort)) {
                NotificationUtils.sendNotification(
                    project,
                    "Current Http port Unavailable ${state.httpPort}",
                    messageType = MessageType.WARNING
                )
                return false
            }
        }

        val params = ArthasUtils.buildBootJarParams(state)
        logger.info("openConsoleByPid. params = [$params]")

        // attach-only
        val commandLine = GeneralCommandLine(javaExecutable)
            .withParameters("-jar", arthasBootPath, pid.toString(), params)
        val processHandler = CapturingProcessHandler(commandLine)
        val output = processHandler.runProcess(10 * 1000, true)
//        val output = processHandler.runProcess(10 * 1000, true)

        if (output.isCancelled || output.isTimeout) {
            return false
        }
        logger.info("openConsoleByPid. $pid, exitCode. ${output.exitCode}")
        logger.info("openConsoleByPid. $pid,stdout. ${output.stdout}")

        if (output.exitCode == 0) {
            // success

            return true
        }
        return false
    }

    /**
     * telnet port 是必须的.
     */
    fun createBootConsole(project: Project, javaExecutable: String, pid: String, telnetPort: Int = 0) {
        logger.info("createBootConsole javaExecutable = $javaExecutable. pid = $pid. telnetPort = $telnetPort")

        val arthasBootPath = ArthasHelperSettings.getInstance().arthasBootPath
        if (arthasBootPath.isEmpty()) {
            logger.warn("Cannot find arthas boot path")
            return
        }
        // 启动一下
        val commandLine = GeneralCommandLine(javaExecutable)
            .withParameters("-jar", arthasBootPath, pid)
        val osHandler = OSProcessHandler(commandLine)

        val processHandler = ColoredProcessHandler(commandLine)
        ToolWindowsUtils.addArthasConsole(project, "arthas-boot-$pid", processHandler)
    }

    fun openTerminalAndRunCommand(project: Project, command: String) {
        val toolWindowManager = TerminalToolWindowManager.getInstance(project)

        val createNewSession1 = toolWindowManager.createNewSession()


        val createNewSession = toolWindowManager.createNewSession()
        createNewSession.sendCommandToExecute(command)


        LocalTerminalStartCommandBuilder.convertShellPathToCommand("telnet.exe")


//        TerminalUtils
//        TerminalUtil.
//        TerminalStarter()


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
