package com.github.yuxiaoyao.arthasideahelper.action

import com.github.yuxiaoyao.arthasideahelper.executor.getArthasExecutorInstance
import com.github.yuxiaoyao.arthasideahelper.runconfig.ArthasUserDataKeys
import com.intellij.execution.RunManager
import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

/**
 * Action to run Java application using the custom ArthasExecutor
 */
class RunWithArthasExecutorAction : AnAction(
    "Run with Arthas Executor",
    "Run Java application using custom Arthas executor",
    null
) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val runManager = RunManager.getInstance(project)

        // Get the currently selected run configuration
        val selectedConfiguration = runManager.selectedConfiguration

        when {
            selectedConfiguration?.configuration is ApplicationConfiguration -> {
                // Enable Arthas for regular ApplicationConfiguration and run with ArthasExecutor
                val originalConfig = selectedConfiguration.configuration as ApplicationConfiguration
                enableArthasAndRun(project, originalConfig)
            }

            else -> {
                Messages.showInfoMessage(
                    project,
                    "Please select a Java Application run configuration first.",
                    "Run with Arthas Executor"
                )
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val runManager = project?.let { RunManager.getInstance(it) }
        val selectedConfiguration = runManager?.selectedConfiguration

        // Enable the action when a Java Application or Arthas configuration is selected
        e.presentation.isEnabled = project != null &&
                (selectedConfiguration?.configuration is ApplicationConfiguration)
    }

    /**
     * Enable Arthas for regular ApplicationConfiguration and run with ArthasExecutor
     */
    private fun enableArthasAndRun(project: Project, originalConfig: ApplicationConfiguration) {
        // Enable Arthas via user data
        originalConfig.putUserData(ArthasUserDataKeys.ARTHAS_ENABLED, true)
        originalConfig.putUserData(ArthasUserDataKeys.ARTHAS_AGENT_PATH, "/my/arthas-agent.jar")

        // Run with ArthasExecutor
        runWithArthasExecutor(project, originalConfig)
    }

    /**
     * Run configuration with ArthasExecutor
     */
    private fun runWithArthasExecutor(project: Project, config: Any) {
        val runManager = RunManager.getInstance(project)
        val arthasExecutor = getArthasExecutorInstance()

        if (arthasExecutor == null) {
            Messages.showErrorDialog(
                project,
                "Arthas Executor not found. Please ensure the plugin is properly installed.",
                "Executor Not Found"
            )
            return
        }

        try {
            when (config) {
                is ApplicationConfiguration -> {
                    // Create temporary settings for ApplicationConfiguration
                    val appConfigType = com.intellij.execution.application.ApplicationConfigurationType()
                    val factory = appConfigType.configurationFactories[0]
                    val settings = runManager.createConfiguration(config, factory)

                    // Execute with the Arthas executor
                    ExecutionUtil.runConfiguration(settings, arthasExecutor)
                }


            }

            // Show success message
            Messages.showInfoMessage(
                project,
                "Started application with Arthas Executor.\n\n" +
                        "Features:\n" +
                        "• Custom Arthas execution environment\n" +
                        "• Enhanced monitoring and debugging\n" +
                        "• Dedicated Arthas tool window integration",
                "Arthas Executor"
            )

        } catch (e: Exception) {
            Messages.showErrorDialog(
                project,
                "Failed to run with Arthas Executor: ${e.message}",
                "Execution Error"
            )
        }
    }
}