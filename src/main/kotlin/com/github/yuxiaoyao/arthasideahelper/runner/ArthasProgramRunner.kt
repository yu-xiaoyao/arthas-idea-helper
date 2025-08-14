package com.github.yuxiaoyao.arthasideahelper.runner

import com.github.yuxiaoyao.arthasideahelper.executor.ArthasExecutor
import com.github.yuxiaoyao.arthasideahelper.pluginId
import com.github.yuxiaoyao.arthasideahelper.runconfig.ArthasUserDataKeys
import com.github.yuxiaoyao.arthasideahelper.util.ArthasUtil
import com.intellij.execution.ExecutionException
import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.DefaultJavaProgramRunner
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.target.TargetEnvironmentAwareRunProfileState
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NonNls
import org.jetbrains.concurrency.Promise

/**
 * @author Kerry2 on 2025/08/07
 */

private val SUPPORTED_EXECUTOR_IDS: MutableSet<String?> = mutableSetOf(
    ArthasExecutor.EXECUTOR_ID,
    DefaultRunExecutor.EXECUTOR_ID
)


class ArthasProgramRunner : DefaultJavaProgramRunner() {

    companion object {
        private val logger = Logger.getInstance(ArthasProgramRunner::class.java)
        const val RUNNER_ID: String = "ArthasProgramRunner"
    }


    override fun getRunnerId(): @NonNls String {
        return RUNNER_ID
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return SUPPORTED_EXECUTOR_IDS.contains(executorId) &&
                // super.canRun(executorId, profile) &&
                (profile is ApplicationConfiguration)
    }

    @Suppress("UnstableApiUsage")
    override fun doExecuteAsync(
        state: TargetEnvironmentAwareRunProfileState,
        env: ExecutionEnvironment
    ): Promise<RunContentDescriptor?> {
        setupArthasEnvironment(env)
        return super.doExecuteAsync(state, env)
    }

    @Throws(ExecutionException::class)
    override fun doExecute(state: RunProfileState, env: ExecutionEnvironment): RunContentDescriptor? {
        setupArthasEnvironment(env)
        return super.doExecute(state, env)
    }

    /**
     * Setup Arthas environment before execution
     */
    private fun setupArthasEnvironment(environment: ExecutionEnvironment) {
        val project = environment.project
        when (val runProfile = environment.runProfile) {
            is ApplicationConfiguration -> {
                val pluginPath = com.intellij.ide.plugins.PluginManagerCore.getPlugin(pluginId)?.pluginPath


                val arthasEnabled =
                    runProfile.getUserData(ArthasUserDataKeys.ARTHAS_ENABLED) ?: ArthasUtil.ARTHAS_ENABLED
                logger.info("Arthas enabled: $arthasEnabled - $pluginPath")
                if (arthasEnabled) {
                    validateArthasAgent(project, ArthasUtil.DEFAULT_ARTHAS_AGENT_PATH)
                    addArthasAgentToConfiguration(runProfile)
                }
            }
        }
    }

    /**
     * Validate that Arthas agent exists and is accessible
     */
    private fun validateArthasAgent(project: Project, agentPath: String) {
        if (!ArthasUtil.validateArthasAgent(agentPath)) {
            val result = Messages.showYesNoDialog(
                project,
                ArthasUtil.getMissingAgentMessage(agentPath) + "\n\nWould you like to select the Arthas agent JAR file?",
                "Arthas Agent Not Found",
                Messages.getQuestionIcon()
            )

            if (result == Messages.YES) {
                selectArthasAgent(project)
            } else {
                throw ExecutionException("Arthas agent not found at: $agentPath")
            }
        }
    }

    /**
     * Allow user to select Arthas agent JAR file
     */
    private fun selectArthasAgent(project: Project) {

        val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
        descriptor.title = "Select Arthas Agent JAR"
        descriptor.description = "Select the arthas-agent.jar file"
        descriptor.withFileFilter { file ->
            file.extension.equals("jar", ignoreCase = true)
        }


        val selectedFile: VirtualFile? = FileChooser.chooseFile(descriptor, null, null)

        if (selectedFile != null) {
            // Here you could store the selected path in project settings or user data
            // For now, we'll just show a message
            Messages.showInfoMessage(
                project,
                "Selected Arthas agent: ${selectedFile.path}\n\nNote: Update the DEFAULT_ARTHAS_AGENT_PATH in the code to use this path permanently.",
                "Arthas Agent Selected"
            )
        } else {
            throw ExecutionException("No Arthas agent selected")
        }
    }

    /**
     * Add Arthas agent to regular ApplicationConfiguration
     */
    private fun addArthasAgentToConfiguration(configuration: ApplicationConfiguration) {
        val agentPath = configuration.getUserData(ArthasUserDataKeys.ARTHAS_AGENT_PATH)
            ?: ArthasUtil.DEFAULT_ARTHAS_AGENT_PATH

        // Use ArthasUtil to add agent parameters
        val currentVmParams = configuration.vmParameters
        val newVmParams = ArthasUtil.addArthasAgentToVmParameters(currentVmParams, agentPath)
        configuration.vmParameters = newVmParams
    }


}
