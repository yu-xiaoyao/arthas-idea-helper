package com.github.yuxiaoyao.arthasideahelper.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.terminal.ui.TerminalWidget
import org.jetbrains.plugins.terminal.TerminalToolWindowManager


/**
 * @author kerryzhang on 2025/08/18
 */


private val logger = Logger.getInstance(ArthasAttachAction::class.java)

class ArthasAttachAction : AnAction("Arthas JVM with Attach", "Attach to Arthas", null) {


    private var terminalSession: TerminalWidget? = null

    override fun actionPerformed(event: AnActionEvent) {
        logger.info("Arthas Attach")

        if (event.project != null) {
            if (terminalSession == null) {
                val toolWindowManager = TerminalToolWindowManager.getInstance(event.project!!)
                terminalSession = toolWindowManager.createNewSession()
            }
            terminalSession?.let {
                if (!it.hasFocus()) {
                    it.requestFocus()
                }
                it.sendCommandToExecute("ls")
            }
//            terminalSession!!.requestFocus()
//            terminalSession!!.sendCommandToExecute("ls")
        }
    }

}