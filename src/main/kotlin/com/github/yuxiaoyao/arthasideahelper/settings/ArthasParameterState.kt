package com.github.yuxiaoyao.arthasideahelper.settings

import com.github.yuxiaoyao.arthasideahelper.utils.ArthasUtils
import com.github.yuxiaoyao.arthasideahelper.utils.SocketUtils
import com.intellij.openapi.project.Project


/**
 * @author kerryzhang on 2025/08/30
 */
data class ArthasParameterState(
    var telnetEnable: Boolean = true,
    var telnetPort: Int = ArthasUtils.DEFAULT_TELNET_PORT,
    var httpEnable: Boolean = false,
    var httpPort: Int = ArthasUtils.DEFAULT_HTTP_PORT,
    var ip: String = SocketUtils.LOCAL_IP,
    var sessionTimeout: Int = ArthasUtils.DEFAULT_SESSION_TIMEOUT,

    var tunnelServerEnable: Boolean = false,

    var appName: String = "",
    var tunnelServerUrl: String = ArthasUtils.DEFAULT_TUNNEL_SERVER,
    var agentId: String = "",
) {

    fun initWithProject(project: Project) {
        if (appName.isEmpty()) {
            appName = project.name
        }
        if (agentId.isEmpty()) {
            agentId = "agentId-${project.name}"
        }
    }

}