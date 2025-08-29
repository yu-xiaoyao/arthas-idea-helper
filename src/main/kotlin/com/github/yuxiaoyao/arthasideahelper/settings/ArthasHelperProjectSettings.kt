package com.github.yuxiaoyao.arthasideahelper.settings

import com.github.yuxiaoyao.arthasideahelper.utils.ArthasUtils
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Project-level settings for Arthas Helper
 * @author kerryzhang on 2025/08/15
 */

@State(
    name = "ArthasHelperProjectSettings",
    storages = [Storage("ArthasHelperProjectSettings.xml")]
)
@Service(Service.Level.PROJECT)
class ArthasHelperProjectSettings : PersistentStateComponent<ArthasHelperProjectSettings> {

    var telnetPort: Int = 0
    var httpPort: Int = 0
    var ip: String = "127.0.0.1"
    var sessionTimeout: Int = ArthasUtils.DEFAULT_SESSION_TIMEOUT

    var tunnelEnable: Boolean = false

    var appName: String = ""
    var tunnelServer: String = ArthasUtils.DEFAULT_TUNNEL_SERVER
    var agentId: String = ""

    /**
     * 最后使用的Jdk
     */
    var lastAttachJdkHome: String = ""


    override fun getState(): ArthasHelperProjectSettings = this

    override fun loadState(state: ArthasHelperProjectSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {

        @JvmStatic
        fun getInstance(project: Project): ArthasHelperProjectSettings {
            val settings = project.getService(ArthasHelperProjectSettings::class.java)
            initializeDefaults(project, settings)
            return settings
        }

        @JvmStatic
        private fun initializeDefaults(project: Project, settings: ArthasHelperProjectSettings) {
            if (settings.appName.isEmpty()) {
                settings.appName = project.name
            }
            if (settings.agentId.isEmpty()) {
                settings.agentId = "agentId-${project.name}"
            }
        }
    }
}