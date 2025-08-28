package com.github.yuxiaoyao.arthasideahelper.utils

import com.github.yuxiaoyao.arthasideahelper.ARTHAS_CONSOLE_TOOL_WINDOW_ID
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.ProcessHandler
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content


/**
 * @author Kerry2 on 2025/08/26
 */

object ToolWindowsUtils {

    fun getArthasToolWindows(project: Project): ToolWindow {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        var toolWindow = toolWindowManager.getToolWindow(ARTHAS_CONSOLE_TOOL_WINDOW_ID)
        if (toolWindow == null) {
            toolWindow = ToolWindowManager.getInstance(project).registerToolWindow(
                ARTHAS_CONSOLE_TOOL_WINDOW_ID,
                {
                    icon = AllIcons.Debugger.Console
                })
        }
        return toolWindow
    }


    fun hasArthasConsole(project: Project, tabName: String): Boolean {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow(ARTHAS_CONSOLE_TOOL_WINDOW_ID) ?: return false
        return toolWindow.contentManager.findContent(tabName) != null
    }


    fun addArthasConsole(
        project: Project,
        tabName: String,
        processHandler: ProcessHandler,
        selected: Boolean = true,
        active: Boolean = true,
        forceAdd: Boolean = false
    ): Boolean {

        val toolWindow = getArthasToolWindows(project)
        var isAdd = false

        var content: Content? = null
        if (!forceAdd) {
            content = toolWindow.contentManager.findContent(tabName)
        }

        // 当前选中的
//        val selectedContent = toolWindow.contentManager.selectedContent


        if (content == null) {
            val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
            consoleView.attachToProcess(processHandler)

            content = toolWindow.contentManager.factory.createContent(consoleView.component, tabName, false)
            toolWindow.contentManager.addContent(content)
            if (selected) {
                toolWindow.contentManager.setSelectedContent(content)
            }
            processHandler.startNotify()
            isAdd = true
        }

        if (active) {
            toolWindow.activate(null)
        }

        return isAdd
    }

}