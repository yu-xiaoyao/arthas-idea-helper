package com.github.yuxiaoyao.arthasideahelper.utils

import com.github.yuxiaoyao.arthasideahelper.settings.ArthasHelperProjectSettings
import com.github.yuxiaoyao.arthasideahelper.settings.ArthasHelperSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.io.File

/**
 * Arthas 工具类
 * @author kerryzhang on 2025/08/14
 */
object ArthasUtils {

    const val LOCAL_IP = "127.0.0.1"

    const val AGENT_JAR = "arthas-agent.jar"
    const val CORE_JAR = "arthas-core.jar"
    const val JAVAAGENT_START = "-javaagent:"
    const val DEFAULT_SESSION_TIMEOUT = 1800
    const val DEFAULT_TUNNEL_SERVER = "ws://127.0.0.1:7777/ws"

    const val ARTHAS_KEY_HTTP_PORT = "httpPort"
    const val ARTHAS_KEY_TELNET_PORT = "telnetPort"
    const val ARTHAS_KEY_SESSION_TIMEOUT = "sessionTimeout"

    const val ARTHAS_KEY_APP_NAME = "appName"
    const val ARTHAS_KEY_PROCESS_ID = "processId"
    const val ARTHAS_KEY_LOG_FILE = "logFile"
    const val ARTHAS_KEY_LOG_LEVEL = "logLevel"
    const val ARTHAS_KEY_CORE_THREAD_COUNT = "coreThreadCount"
    const val ARTHAS_KEY_QUEUE_CAPACITY = "queueCapacity"
    const val ARTHAS_KEY_BATCH_SIZE = "batchSize"
    const val ARTHAS_KEY_BATCH_INTERVAL = "batchInterval"

    /**
     * 获取 Arthas Agent 路径
     */
    fun getArthasAgentPath(): String {
        return ArthasHelperSettings.getInstance().arthasAgentPath
    }

    /**
     * 检查 Arthas Agent 路径是否有效
     */
    fun isArthasAgentPathValid(): Boolean {
        val path = getArthasAgentPath()
        if (path.isBlank()) {
            return false
        }

        val file = File(path)
        return file.exists() && file.isFile && file.name.endsWith(".jar")
    }

    /**
     * 验证 Arthas Agent 路径，如果无效则显示错误消息
     */
    fun validateArthasAgentPath(project: Project?): Boolean {
        if (!isArthasAgentPathValid()) {
            Messages.showErrorDialog(
                project,
                "请先在设置中配置有效的 arthas-agent.jar 路径\n" +
                        "路径: File -> Settings -> Tools -> Arthas Idea Helper",
                "Arthas Agent 路径未配置"
            )
            return false
        }
        return true
    }

    /**
     * 获取 Arthas Agent 启动命令参数
     */
    fun getArthasAgentJvmArgs(): String {
        val agentPath = getArthasAgentPath()
        return if (agentPath.isNotBlank()) {
            "-javaagent:$agentPath"
        } else {
            ""
        }
    }


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


    fun buildProjectAgentParams(project: Project): String {
        val args = mutableListOf<String>()
        val projectSettings = ArthasHelperProjectSettings.getInstance(project)


        if (projectSettings.httpPort == -1) {
            args.add("${ARTHAS_KEY_HTTP_PORT}=${projectSettings.httpPort}")
        } else if (projectSettings.httpPort == 0) {
            val findAvailablePort = NetworkUtil.findAvailablePort()
            projectSettings.httpPort = findAvailablePort
            args.add("${ARTHAS_KEY_HTTP_PORT}=$findAvailablePort")
        }

        if (projectSettings.telnetPort == -1) {
            args.add("${ARTHAS_KEY_TELNET_PORT}=${projectSettings.telnetPort}")
        } else if (projectSettings.telnetPort == 0) {
            val findAvailablePort = NetworkUtil.findAvailablePort()
            projectSettings.telnetPort = findAvailablePort
            args.add("${ARTHAS_KEY_TELNET_PORT}=$findAvailablePort")
        }

        if (projectSettings.ip != LOCAL_IP) {
            args.add("ip=${projectSettings.ip}")
        }

        if (projectSettings.sessionTimeout != DEFAULT_SESSION_TIMEOUT) {
            args.add("${ARTHAS_KEY_SESSION_TIMEOUT}=${projectSettings.sessionTimeout}")
        }

        val paramLine = args.joinToString(";")


        val settings = ArthasHelperSettings.getInstance()
        if (settings.arthasCorePath.isNotBlank()) {
            if (settings.arthasCorePath.contains(" ")) {
                return """"${settings.arthasCorePath}";$paramLine"""
            }
            return "${settings.arthasCorePath};$paramLine"
        }
        return ";$paramLine"
    }
}