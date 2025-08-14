package com.github.yuxiaoyao.arthasideahelper.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.io.File

/**
 * Utility class for Arthas-related operations
 */
object ArthasUtil {

    const val ARTHAS_ENABLED = true

    // const val DEFAULT_ARTHAS_AGENT_PATH = "D:\\Code\\github-yu\\arthas-idea-plugin\\lib\\arthas\\arthas-agent.jar"
    const val DEFAULT_ARTHAS_AGENT_PATH = "C:\\App\\0DevApp\\arthas-4.0.5\\arthas-agent.jar"

    /**
     * Validate if Arthas agent exists at the given path
     */
    fun validateArthasAgent(agentPath: String): Boolean {
        return File(agentPath).exists()
    }

    /**
     * Get a user-friendly error message for missing Arthas agent
     */
    fun getMissingAgentMessage(agentPath: String): String {
        return "Arthas agent not found at: $agentPath\n\n" +
                "Please ensure the Arthas agent JAR file exists at the specified location.\n" +
                "You can download it from: https://github.com/alibaba/arthas/releases"
    }

    /**
     * Show error dialog for missing Arthas agent
     */
    fun showMissingAgentDialog(project: Project, agentPath: String) {
        Messages.showErrorDialog(
            project,
            getMissingAgentMessage(agentPath),
            "Arthas Agent Not Found"
        )
    }

    /**
     * Check if a VM parameters string already contains Arthas agent
     */
    fun containsArthasAgent(vmParameters: String?): Boolean {
        return vmParameters?.contains("-javaagent:") == true &&
                vmParameters.contains("arthas")
    }

    /**
     * Add Arthas agent to VM parameters if not already present
     */
    fun addArthasAgentToVmParameters(vmParameters: String?, agentPath: String): String {
        if (containsArthasAgent(vmParameters)) {
            return vmParameters ?: ""
        }

        val agentParam = "-javaagent:$agentPath"
        return if (vmParameters.isNullOrEmpty()) {
            agentParam
        } else {
            "$vmParameters $agentParam"
        }
    }

    /**
     * Generate unique Arthas agent ID for debugging
     */
    fun generateArthasAgentId(): String {
        return "arthas-${System.currentTimeMillis()}"
    }
}