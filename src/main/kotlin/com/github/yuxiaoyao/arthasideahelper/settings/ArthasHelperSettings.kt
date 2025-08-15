package com.github.yuxiaoyao.arthasideahelper.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * @author kerryzhang on 2025/08/14
 */

@State(
    name = "ArthasHelperSettings",
    storages = [Storage("arthas-helper.xml")]
)
@Service
class ArthasHelperSettings : PersistentStateComponent<ArthasHelperSettings> {

    var arthasHome: String = ""
    var arthasAgentPath: String = ""
    var arthasCorePath: String = ""

    override fun getState(): ArthasHelperSettings = this

    override fun loadState(state: ArthasHelperSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): ArthasHelperSettings {
            return ApplicationManager.getApplication().getService(ArthasHelperSettings::class.java)
        }
    }
}