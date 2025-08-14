package com.github.yuxiaoyao.arthasideahelper.runner

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.executor.ArthasExecutor
import com.github.yuxiaoyao.arthasideahelper.settings.ArthasHelperSearchableConfigurable
import com.github.yuxiaoyao.arthasideahelper.settings.ArthasHelperSettings
import com.github.yuxiaoyao.arthasideahelper.utils.ArthasUtils
import com.intellij.execution.ExecutionException
import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.configurations.*
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.DefaultJavaProgramRunner
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.target.TargetEnvironmentAwareRunProfileState
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rd.framework.base.deepClonePolymorphic
import org.jetbrains.annotations.NonNls
import org.jetbrains.concurrency.Promise
import java.io.File


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

    override fun doExecuteAsync(
        state: TargetEnvironmentAwareRunProfileState,
        env: ExecutionEnvironment
    ): Promise<RunContentDescriptor?> {
        logger.info("doExecuteAsync. ${state}")

        if (state is JavaCommandLineState) {
            val wrappedState: RunProfileState = object : JavaCommandLineState(env) {
                @Throws(ExecutionException::class)
                override fun createJavaParameters(): JavaParameters {
                    val params: JavaParameters = state.javaParameters()
                    params.getVMParametersList().add("-Dmy.custom.option=value")
                    return params
                }
            }
        }

        val newEnv = setupArthasEnvironment(env)
        return super.doExecuteAsync(state, newEnv)
    }

    @Throws(ExecutionException::class)
    override fun doExecute(state: RunProfileState, env: ExecutionEnvironment): RunContentDescriptor? {
        val newEnv = setupArthasEnvironment(env)
        return super.doExecute(state, newEnv)
    }

    /**
     * Setup Arthas environment before execution
     */
    private fun setupArthasEnvironment(environment: ExecutionEnvironment): ExecutionEnvironment {
        val project = environment.project
        when (val runProfile = environment.runProfile) {
            is ApplicationConfiguration -> {
                val arthasAgentPath = ArthasHelperSettings.getInstance().arthasAgentPath

                if (arthasAgentPath.isBlank()) {
                    openSettings(project)
                    return environment
                }

                val file = File(arthasAgentPath)
                if (!file.exists() || !file.extension.endsWith("jar", ignoreCase = true)) {
                    openSettings(project)
                    return environment
                }

                logger.info("runProfile.vmParameters = ${runProfile.vmParameters}")

                val newVmParams = buildVmParameters(runProfile.vmParameters, arthasAgentPath)

//                logger.info("newVmParams = $newVmParams")

                if (newVmParams != null) {
                    val deepClonePolymorphic = environment.deepClonePolymorphic()
                    (deepClonePolymorphic.runProfile as ApplicationConfiguration).vmParameters = newVmParams
                    return deepClonePolymorphic
                }
            }
        }
        return environment
    }


    private fun buildVmParameters(vmParams: String?, agentPath: String): String? {
        val currentVmParams = vmParams ?: ""
        val args = mutableSetOf<String>()
        val tokenizer = CommandLineTokenizer(currentVmParams)

        var addAgent = true
        while (tokenizer.hasMoreTokens()) {
            val token = tokenizer.nextToken()
            if (token.startsWith("-javaagent:")) {
                val currentAgentPath = token.substring("-javaagent:".length)
                logger.info("configuration.vmParameters. currentAgentPath = $currentAgentPath")
                val filename = getFileNameUsingSubstringAfterLast(currentAgentPath)
                if (ArthasUtils.AGENT_JAR == filename) {
                    if (File(currentAgentPath).exists()) {
                        addAgent = false
                        args.add(currentAgentPath)
                    }
                }
            } else {
                args.add(token)
            }
        }

        if (addAgent) {
            return ArthasUtils.addArthasAgentToVmParameters(args.joinToString(" "), agentPath)
        }
        return null
    }

    fun getFileNameUsingSubstringAfterLast(path: String): String {
        return path.substringAfterLast('\\').substringAfterLast('/')
    }

    private fun openSettings(project: Project) {
        val result = Messages.showYesNoDialog(
            project,
            MyBundle.message("agent.missing.message"),
            MyBundle.message("agent.missing"),
            Messages.getQuestionIcon()
        )
        if (result == Messages.YES) {
            ShowSettingsUtil.getInstance().showSettingsDialog(
                project,
                ArthasHelperSearchableConfigurable::class.java
            )
            //TODO show Notication
            logger.info("Arthas agent not found, open settings: " + ArthasHelperSettings.getInstance().arthasAgentPath)
        } /*else {
            // skip
            throw ExecutionException("Arthas agent not found")
        }*/

    }


    /**
     * Validate that Arthas agent exists and is accessible
     */
    private fun validateArthasAgent(project: Project, agentPath: String) {
        if (!ArthasUtils.validateArthasAgent(agentPath)) {
            val result = Messages.showYesNoDialog(
                project,
                ArthasUtils.getMissingAgentMessage(agentPath) + "\n\nWould you like to select the Arthas agent JAR file?",
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

}
