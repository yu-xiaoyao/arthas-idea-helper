package com.github.yuxiaoyao.arthasideahelper.settings

import com.github.yuxiaoyao.arthasideahelper.PROJECT_STORAGE_FILE
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project


/**
 * @author kerryzhang on 2025/08/30
 */


@State(
    name = "ArthasAttachSettings",
    storages = [Storage(PROJECT_STORAGE_FILE)]
)
@Service(Service.Level.PROJECT)
class ArthasAttachSettings : PersistentStateComponent<ArthasParameterState> {

    private var state: ArthasParameterState = ArthasParameterState()

    override fun getState(): ArthasParameterState {
        return state
    }

    override fun loadState(state: ArthasParameterState) {
        this.state = state
    }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): ArthasAttachSettings {
            val settings = project.getService(ArthasAttachSettings::class.java)
            settings.state.initWithProject(project)
            return settings
        }
    }
}