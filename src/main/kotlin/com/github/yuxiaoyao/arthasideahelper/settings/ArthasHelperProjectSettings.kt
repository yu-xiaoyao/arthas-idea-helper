package com.github.yuxiaoyao.arthasideahelper.settings

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
    storages = [Storage("arthas-helper-project.xml")]
)
@Service(Service.Level.PROJECT)
class ArthasHelperProjectSettings(private val project: Project) :
    PersistentStateComponent<ArthasHelperProjectSettings> {

    var telnetPort: Int = 3658
    var httpPort: Int = 8563
    var ip: String = "127.0.0.1"
    var sessionTimeout: Int = 1800

    var tunnelEnable: Boolean = false

    var appName: String = ""
    var tunnelServer: String = "ws://127.0.0.1:7777/ws"
    var agentId: String = ""

    private var defaultsInitialized: Boolean = false

    init {
        initializeDefaults()
    }

    private fun initializeDefaults() {
        if (!defaultsInitialized && appName.isEmpty() && agentId.isEmpty()) {
            val projectName = project.name
            appName = projectName
            agentId = "id-$projectName"
            defaultsInitialized = true
        }
    }

    override fun getState(): ArthasHelperProjectSettings = this

    override fun loadState(state: ArthasHelperProjectSettings) {
        XmlSerializerUtil.copyBean(state, this)
        // After loading state, ensure defaults are set if values are still empty
        initializeDefaults()
    }

    companion object {
        fun getInstance(project: Project): ArthasHelperProjectSettings {
            return project.getService(ArthasHelperProjectSettings::class.java)
        }
    }
}