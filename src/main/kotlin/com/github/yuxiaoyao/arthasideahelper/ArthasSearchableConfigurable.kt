package com.github.yuxiaoyao.arthasideahelper

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent


/**
 * @author kerryzhang on 2025/08/14
 */

class ArthasSearchableConfigurable : SearchableConfigurable {

    override fun getId(): @NonNls String {
        return PLUGIN_ID
    }

    override fun getDisplayName(): @NlsContexts.ConfigurableName String {
        return MyBundle.message("pluginName")
    }

    override fun createComponent(): JComponent? {
        TODO("Not yet implemented")
    }

    override fun isModified(): Boolean {
        TODO("Not yet implemented")
    }

    override fun apply() {
        TODO("Not yet implemented")
    }
}