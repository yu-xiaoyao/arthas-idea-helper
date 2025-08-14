package com.github.yuxiaoyao.arthasideahelper.settings

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.PLUGIN_ID
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.annotations.NonNls
import javax.swing.*


/**
 * @author kerryzhang on 2025/08/14
 */

class ArthasHelperSearchableConfigurable : SearchableConfigurable {

    private val mainPanel: JPanel
    private val usernameField: JTextField
    private val enableFeatureCheckBox: JCheckBox

    init {
        mainPanel = JPanel()
        mainPanel.setLayout(BoxLayout(mainPanel, BoxLayout.Y_AXIS))

        usernameField = JTextField()
        enableFeatureCheckBox = JCheckBox("Enable Feature")

        mainPanel.add(JLabel("Username:"))
        mainPanel.add(usernameField)
        mainPanel.add(enableFeatureCheckBox)
    }

    override fun getId(): @NonNls String {
        return PLUGIN_ID
    }

    override fun getDisplayName(): @NlsContexts.ConfigurableName String {
        return MyBundle.message("pluginName")
    }

    override fun createComponent(): JComponent? {
        reset(); // 初始化 UI
        return mainPanel;
    }

    override fun isModified(): Boolean {
        // 判断 UI 值是否和当前配置不同
        return !usernameField.getText().equals(ArthasHelperSettings.username) ||
                enableFeatureCheckBox.isSelected != ArthasHelperSettings.enableFeature;
    }

    override fun apply() {
        ArthasHelperSettings.username = usernameField.getText();
        ArthasHelperSettings.enableFeature = enableFeatureCheckBox.isSelected
    }

    override fun reset() {
        usernameField.text = ArthasHelperSettings.username;
        enableFeatureCheckBox.setSelected(ArthasHelperSettings.enableFeature)
    }
}