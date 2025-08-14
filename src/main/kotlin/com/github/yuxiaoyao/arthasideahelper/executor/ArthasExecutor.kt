package com.github.yuxiaoyao.arthasideahelper.executor

import com.intellij.execution.Executor
import com.intellij.execution.ExecutorRegistry
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.TextWithMnemonic
import com.intellij.openapi.wm.ToolWindowId
import javax.swing.Icon

/**
 * Custom executor for running Java applications with Arthas agent
 */
class ArthasExecutor : Executor() {

    companion object {
        const val EXECUTOR_ID = "ArthasExecutor"
        const val EXECUTOR_ACTION_NAME = "RunWithArthas"

    }

    override fun getToolWindowId(): String {
        // Use the standard Run tool window
        return ToolWindowId.RUN
//        return id
    }

    override fun getToolWindowIcon(): Icon {
        // Use a distinctive icon for Arthas execution
        return AllIcons.Actions.Execute
    }

    override fun getIcon(): Icon {
        // Use a custom icon that represents Arthas + execution
        return AllIcons.Actions.Execute
    }

    override fun getDisabledIcon(): Icon? {
        return AllIcons.Actions.Resume
    }

    override fun getDescription(): String {
        return "Run Java application with Arthas agent for debugging and monitoring"
    }

    override fun getActionName(): String {
        return EXECUTOR_ACTION_NAME
    }

    override fun getId(): String {
        return EXECUTOR_ID
    }

    override fun getStartActionText(): String {
        return "Run with Arthas"
    }

    override fun getStartActionText(configurationName: String): String {
        // 这里是 Run 菜单下的 Run with Arthas 选项
        return "Run '$configurationName' with Arthas"
    }

    override fun getContextActionId(): String {
        return "RunWithArthasContext"
    }

    override fun getHelpId(): String? {
        return "reference.dialogs.rundebug.ArthasExecutor"
    }

    /**
     * Determines if this executor is applicable for the current context
     */
    override fun isApplicable(project: Project): Boolean {
        return true
    }

    override fun isSupportedOnTarget(): Boolean {
        return true
    }

    /**
     * Get the text with mnemonic for the executor action
     */
    fun getStartActionTextWithMnemonic(): TextWithMnemonic {
        return TextWithMnemonic.parse("Run with &Arthas")
    }

    /**
     * Get the text with mnemonic for a specific configuration
     */
    fun getStartActionTextWithMnemonic(configurationName: String): TextWithMnemonic {
        return TextWithMnemonic.parse("Run '$configurationName' with &Arthas")
    }


}

/**
 * Get the ArthasExecutor instance
 */
fun getArthasExecutorInstance(): ArthasExecutor? {
    return ExecutorRegistry.getInstance().getExecutorById(ArthasExecutor.EXECUTOR_ID) as? ArthasExecutor
}