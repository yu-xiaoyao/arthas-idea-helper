package com.github.yuxiaoyao.arthasideahelper.action

import com.github.yuxiaoyao.arthasideahelper.utils.TelnetCommandExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.platform.ide.progress.ModalTaskOwner.project
import org.jetbrains.plugins.terminal.LocalTerminalDirectRunner
import org.jetbrains.plugins.terminal.ShellStartupOptions
import org.jetbrains.plugins.terminal.TerminalToolWindowManager


/**
 * @author kerryzhang on 2025/08/18
 */


private val logger = Logger.getInstance(ArthasAttachAction::class.java)

class ArthasAttachAction : AnAction("Arthas JVM with Attach", "Attach to Arthas", null) {


    override fun actionPerformed(event: AnActionEvent) {
        logger.info("Arthas Attach")

        if (event.project != null) {

            val executor = TelnetCommandExecutor(event.project)
            val host = "localhost"
            val port = 23
            val commands = arrayOf<String?>(
                "help",
                "ls",
                "pwd"
            )

//            executor.executeCommandSequence(host, port)


            val runner = LocalTerminalDirectRunner.createTerminalRunner(event.project)

            logger.info("runner: $runner")

            val options = ShellStartupOptions.Builder()
                .workingDirectory("C:\\Users\\Kerry2\\Desktop\\TDST")
                .shellCommand(listOf("telnet 127.0.0.1 3658"))
                .build()


            val process = runner.createProcess(options)
            val connector = runner.createTtyConnector(process)


            val toolWindowManager = TerminalToolWindowManager.getInstance(event.project!!)

            val createNewSession = toolWindowManager.createNewSession()

            createNewSession.connectToTty(connector, com.jediterm.core.util.TermSize(80, 24))

            createNewSession.requestFocus()

//            toolWindowManager.createNewSession(runner)

            /*           toolWindowManager.createNewSession(runner)

                       val terminalSession = toolWindowManager.createNewSession()
                       terminalSession.let {
                           if (!it.hasFocus()) {
                               it.requestFocus()
                           }
                           it.sendCommandToExecute("ls")
                       }*/


//            terminalSession!!.requestFocus()
//            terminalSession!!.sendCommandToExecute("ls")
        }
    }

}