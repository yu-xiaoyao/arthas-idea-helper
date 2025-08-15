package com.github.yuxiaoyao.arthasideahelper.settings

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel

/**
 * @author kerryzhang on 2025/08/14
 */
class ArthasHelperConfigurable : BoundConfigurable(MyBundle.message("pluginName")) {

    private val settings = ArthasHelperSettings.getInstance()


    override fun createPanel(): DialogPanel {
        val fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor().apply {
            title = MyBundle.message("settings.arthasAgentPath.chooser")
            description = MyBundle.message("settings.arthasAgentPath.comment")
            withFileFilter { file ->
                file.extension.equals("jar", ignoreCase = true)
            }
        }

        return panel {
            row(MyBundle.message("settings.arthasAgentPath") + ":") {
                textFieldWithBrowseButton(
                    fileChooserDescriptor,
                    null,
                    fileChosen = { vf -> vf.path }
                )
                    .bindText(settings::arthasAgentPath)
                    .comment(MyBundle.message("settings.arthasAgentPath.comment"))
                    .align(AlignX.FILL)
                    .validationOnInput {
                        validateJarPath(it.text)
                    }
                    .validationOnApply {
                        validateJarPath(it.text)
                    }
            }
        }
    }

    private fun validateJarPath(path: String): ValidationInfo? {
        if (path.isBlank()) {
            // 路径不能为空
            return ValidationInfo(MyBundle.message("file.pathEmpty"))
        }
        val file = java.io.File(path)
        if (!file.exists() || !file.isFile) {
            // 文件不存在
            return ValidationInfo(MyBundle.message("file.fileNotExist"))
        }
        if (!path.endsWith(".jar", ignoreCase = true)) {
            // 必须选择 .jar 文件
            return ValidationInfo(MyBundle.message("file.fileNotJar"))
        }
        return null
    }

}