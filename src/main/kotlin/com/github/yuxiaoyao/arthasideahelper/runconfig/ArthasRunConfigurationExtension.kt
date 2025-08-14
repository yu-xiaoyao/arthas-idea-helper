package com.github.yuxiaoyao.arthasideahelper.runconfig

import com.intellij.execution.RunConfigurationExtension
import com.intellij.execution.application.ApplicationConfiguration
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.openapi.options.SettingsEditor
import org.jdom.Element
import java.awt.BorderLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Extension for adding Arthas agent to Java run configurations
 */
class ArthasRunConfigurationExtension : RunConfigurationExtension() {


    override fun <T : RunConfigurationBase<*>?> updateJavaParameters(
        configuration: T,
        params: JavaParameters,
        runnerSettings: RunnerSettings?
    ) {
        if (configuration is ApplicationConfiguration && isArthasEnabled(configuration)) {
            val agentPath = getArthasAgentPath(configuration)
            params.vmParametersList.add("-javaagent:$agentPath")
        }
    }

    override fun isApplicableFor(configuration: RunConfigurationBase<*>): Boolean {
        return configuration is ApplicationConfiguration
    }

    override fun <T : RunConfigurationBase<*>?> createEditor(configuration: T): SettingsEditor<T>? {
        return if (configuration is ApplicationConfiguration) {
            ArthasSettingsEditor() as SettingsEditor<T>
        } else {
            null
        }
    }

    override fun getEditorTitle(): String {
        return "Arthas"
    }

    override fun readExternal(runConfiguration: RunConfigurationBase<*>, element: Element) {
        if (runConfiguration is ApplicationConfiguration) {

        }
    }

    override fun writeExternal(runConfiguration: RunConfigurationBase<*>, element: Element) {
        if (runConfiguration is ApplicationConfiguration) {
            val enabled = isArthasEnabled(runConfiguration)
            val agentPath = getArthasAgentPath(runConfiguration)


        }
    }

    private fun isArthasEnabled(configuration: ApplicationConfiguration): Boolean {
        return configuration.getUserData(ArthasUserDataKeys.ARTHAS_ENABLED) ?: false
    }

    private fun getArthasAgentPath(configuration: ApplicationConfiguration): String {
        return ""
    }

    /**
     * Settings editor for Arthas configuration
     */
    private class ArthasSettingsEditor : SettingsEditor<RunConfigurationBase<*>>() {

        private val enableArthasCheckBox = JCheckBox("Enable Arthas Agent")
        private val panel = JPanel(BorderLayout())

        init {
            panel.add(enableArthasCheckBox, BorderLayout.NORTH)
        }

        override fun resetEditorFrom(configuration: RunConfigurationBase<*>) {
            if (configuration is ApplicationConfiguration) {
                enableArthasCheckBox.isSelected = configuration.getUserData(ArthasUserDataKeys.ARTHAS_ENABLED) ?: false
            }
        }

        override fun applyEditorTo(configuration: RunConfigurationBase<*>) {
            if (configuration is ApplicationConfiguration) {
                configuration.putUserData(ArthasUserDataKeys.ARTHAS_ENABLED, enableArthasCheckBox.isSelected)
                if (enableArthasCheckBox.isSelected) {

                }
            }
        }

        override fun createEditor(): JComponent {
            return panel
        }
    }
}