package com.github.yuxiaoyao.arthasideahelper.utils


import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.settings.ArthasHelperSettings
import com.github.yuxiaoyao.arthasideahelper.settings.ArthasParameterState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.OSProcessHandler
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import org.jetbrains.plugins.terminal.TerminalToolWindowManager
import org.jetbrains.plugins.terminal.runner.LocalTerminalStartCommandBuilder


/**
 * @author kerryzhang on 2025/08/16
 */

private val logger = Logger.getInstance(ArthasTerminalUtils::class.java)

object ArthasTerminalUtils {

    fun executeByPid(
        project: Project,
        javaExecutable: String,
        pid: Any,
        state: ArthasParameterState,
        commandTimeout: Long = 10 * 1000L
    ): Boolean {

        logger.info("openConsoleByPid. javaExecutable = $javaExecutable, pid = $pid. commandTimeout = $commandTimeout")

        val arthasBootPath = ArthasHelperSettings.getInstance().arthasBootPath
        if (arthasBootPath.isEmpty()) {
            Messages.showWarningDialog(project, "Cannot find arthas boot path", MyBundle.message("pluginName"))
            return false
        }
        if (state.telnetEnable) {
            if (state.telnetPort != NetworkUtil.checkPortAvailable(state.telnetPort)) {
                Messages.showWarningDialog(
                    project,
                    "Current Telnet port Unavailable ${state.telnetPort}",
                    MyBundle.message("pluginName")
                )
                return false
            }
        }
        if (state.httpEnable) {
            if (state.httpPort != NetworkUtil.checkPortAvailable(state.httpPort)) {
                Messages.showWarningDialog(
                    project,
                    "Current Http port Unavailable ${state.telnetPort}",
                    MyBundle.message("pluginName")
                )
                return false
            }
        }

        val params = ArthasUtils.buildBootJarParams(state)
        logger.info("openConsoleByPid. params = [$params]")

        // attach-only
        val commandLine = GeneralCommandLine(javaExecutable)
            .withParameters("-jar", arthasBootPath, pid.toString())
        commandLine.addParameters(params)

        logger.info("Run command. commandTimeout = $commandTimeout. ${commandLine.commandLineString}")

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Attach Arthas $pid") {
            override fun run(indicator: ProgressIndicator) {
                val processHandler = CapturingProcessHandler(commandLine)
                val output = if (commandTimeout > 0L)
                    processHandler.runProcess(commandTimeout.toInt(), true)
                else
                    processHandler.runProcess()

                logger.info("命令结果. $pid, exitCode: ${output.exitCode}. $output")

                if (output.exitCode != 0 || output.isTimeout || output.isCancelled) {
//                    ApplicationManager.getApplication().invokeLater({
//                        Messages.showErrorDialog(project, "Attached Arthas $pid", "Attached Arthas $pid")
//                    }, ModalityState.any())
                    /*
                    SwingUtilities.invokeLater {
                        Messages.showErrorDialog(project, "Attached Arthas $pid", "Attached Arthas $pid")
                    }
                     */
                } else {
//                    ApplicationManager.getApplication().invokeLater({
//                        Messages.showInfoMessage(project, "Attached Arthas $pid", "Attached Arthas $pid")
//                    }, ModalityState.any())

                    NotificationUtils.sendNotification(project, "TTTTTTTTTTTT")

                    /*
                    SwingUtilities.invokeLater {
                        Messages.showInfoMessage(project, "Attached Arthas $pid", "Attached Arthas $pid")
                    }
                     */
                }
            }

        })
        return true
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
