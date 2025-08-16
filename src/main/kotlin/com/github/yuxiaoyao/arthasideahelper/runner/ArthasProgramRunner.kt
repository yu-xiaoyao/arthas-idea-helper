package com.github.yuxiaoyao.arthasideahelper.runner

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.executor.ArthasExecutor
import com.github.yuxiaoyao.arthasideahelper.settings.ArthasHelperConfigurable
import com.github.yuxiaoyao.arthasideahelper.settings.ArthasHelperSettings
import com.github.yuxiaoyao.arthasideahelper.utils.ArthasUtils
import com.intellij.execution.ExecutionException
import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.configurations.CommandLineTokenizer
import com.intellij.execution.configurations.JavaCommandLineState
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

    override fun execute(environment: ExecutionEnvironment) {
        super.execute(environment)

    }

    override fun doExecuteAsync(
        state: TargetEnvironmentAwareRunProfileState,
        env: ExecutionEnvironment
    ): Promise<RunContentDescriptor?> {
        if (state is JavaCommandLineState) {
            val arthasAgentPath = getArthasAgentPath(env.project)
            if (arthasAgentPath.isNotEmpty()) {
                val agentParms = ArthasUtils.buildProjectAgentParams(env.project)

                logger.info("javaagent parameters: $agentParms")

                val arthasAgent = if (arthasAgentPath.contains(" ")) {
                    """${ArthasUtils.JAVAAGENT_START}"$arthasAgentPath"=$agentParms"""
                } else {
                    "${ArthasUtils.JAVAAGENT_START}$arthasAgentPath=$agentParms"
                }
                val javaParameters = state.javaParameters
                // 以下代码想要将原来已有 arthas-agent.jar 配置替换...
                /*
                val vmParametersList = javaParameters.vmParametersList
                var hasArthasAgent = false

                val removeIndex = mutableListOf<Int>()

                for ((index, param) in vmParametersList.parameters.withIndex()) {
                    if (param.startsWith(ArthasUtils.JAVAAGENT_START)) {
                        val javaagentParams = param.substringAfter(ArthasUtils.JAVAAGENT_START)
                        val javaagentPath = javaagentParams.substringBefore("=")

                        val agentName = getFileNameUsingSubstringAfterLast(javaagentPath)

                        if (agentName == ArthasUtils.AGENT_JAR) {
                            val agentFile = File(javaagentPath)
                            if (agentFile.exists() && agentFile.isFile) {
                                hasArthasAgent = true
                            } else {
                                removeIndex.add(index)
                            }
                        }
                    }
                }
                if (removeIndex.isNotEmpty()) {
                    removeIndex.forEach {
                        vmParametersList.parameters.removeAt(it)
                    }
                }
                logger.info("vmParametersList.parameters = ${vmParametersList.parameters}")
                if (!hasArthasAgent) {
                    javaParameters.vmParametersList.addAt(0, arthasAgent)
                    vmParametersList.parameters.addFirst(arthasAgent)
                }
                logger.info("javaParameters = ${javaParameters.vmParametersList}")
                */

                javaParameters.vmParametersList.addAt(0, arthasAgent)
            }
        }
        return super.doExecuteAsync(state, env)
    }

    @Throws(ExecutionException::class)
    override fun doExecute(state: RunProfileState, env: ExecutionEnvironment): RunContentDescriptor? {
        // 未测试, 实际好像不会走这里?
        if (state is JavaCommandLineState) {
            val arthasAgentPath = getArthasAgentPath(env.project)
            if (arthasAgentPath.isNotEmpty()) {
                val arthasAgent = "${ArthasUtils.JAVAAGENT_START}$arthasAgentPath"
                val javaParameters = state.javaParameters
                javaParameters.vmParametersList.addAt(0, arthasAgent)
            }
        }
        return super.doExecute(state, env)
    }

    private fun getArthasAgentPath(project: Project): String {
        val arthasAgentPath = ArthasHelperSettings.getInstance().arthasAgentPath
        if (arthasAgentPath.isBlank()) {
            return openSettingsConfigAgentPath(project)
        }
        val file = File(arthasAgentPath)
        if (!file.exists()) {
            return openSettingsConfigAgentPath(project)
        }
        if (!file.extension.endsWith("jar", ignoreCase = true)) {
            return openSettingsConfigAgentPath(project)
        }
        return arthasAgentPath
    }


    /**
     * 这种方式会修改 Configuration 中的配置
     */
    private fun setupArthasEnvironment(environment: ExecutionEnvironment): ExecutionEnvironment {
        val project = environment.project
        when (val runProfile = environment.runProfile) {
            is ApplicationConfiguration -> {
                val arthasAgentPath = ArthasHelperSettings.getInstance().arthasAgentPath

                if (arthasAgentPath.isBlank()) {
                    openSettingsConfigAgentPath(project)
                    return environment
                }

                val file = File(arthasAgentPath)
                if (!file.exists() || !file.extension.endsWith("jar", ignoreCase = true)) {
                    openSettingsConfigAgentPath(project)
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


    private fun isValidArthasAgent(path: String): Boolean {
        val javaagentPath = path.substringBefore("=")
        val filename = getFileNameUsingSubstringAfterLast(javaagentPath)
        if (ArthasUtils.AGENT_JAR == filename) {
            val af = File(javaagentPath)
            if (af.exists()) {
                return true
            }
        }
        return false
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

    private fun openSettingsConfigAgentPath(project: Project): String {
        val result = Messages.showYesNoDialog(
            project,
            MyBundle.message("agent.missing.message"),
            MyBundle.message("agent.missing"),
            Messages.getQuestionIcon()
        )
        if (result == Messages.YES) {
            ShowSettingsUtil.getInstance().showSettingsDialog(
                project,
                ArthasHelperConfigurable::class.java
            )
            //TODO show Notication
            logger.info("Arthas agent not found, open settings: " + ArthasHelperSettings.getInstance().arthasAgentPath)
        } /*else {
            // skip
            throw ExecutionException("Arthas agent not found")
        }*/
        return ArthasHelperSettings.getInstance().arthasAgentPath
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
