package com.github.yuxiaoyao.arthasideahelper.settings

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.utils.ArthasUtils
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.ui.dsl.builder.*

/**
 * @author kerryzhang on 2025/08/14
 */
class ArthasHelperConfigurable : BoundConfigurable(MyBundle.message("pluginName")) {

    private val settings = ArthasHelperSettings.getInstance()


    override fun createPanel(): DialogPanel {
        return panel {

            lateinit var tfAgentJar: Cell<TextFieldWithBrowseButton>
            lateinit var tfBootJar: Cell<TextFieldWithBrowseButton>
            lateinit var tfCoreJar: Cell<TextFieldWithBrowseButton>

            row(MyBundle.message("settings.arthasHome") + ":") {
                val folderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor().apply {
                    title = MyBundle.message("settings.arthasHome")
                }

                textFieldWithBrowseButton(
                    folderDescriptor,
                    null,
                    fileChosen = { vf ->
                        val jarFiles = VfsUtilCore.virtualToIoFile(vf)
                            .walk()
                            .filter { it.isFile && (it.name == ArthasUtils.AGENT_JAR || it.name == ArthasUtils.BOOT_JAR || it.name == ArthasUtils.CORE_JAR) }
                            .toList()

                        jarFiles.forEach { jar ->
                            if (jar.name == ArthasUtils.AGENT_JAR) {
                                tfAgentJar.text(jar.path)
                            } else if (jar.name == ArthasUtils.BOOT_JAR) {
                                tfBootJar.text(jar.path)
                            } else if (jar.name == ArthasUtils.CORE_JAR) {
                                tfCoreJar.text(jar.path)
                            }
                        }

                        vf.path
                    }
                )
                    .bindText(settings::arthasHome)
                    .comment(MyBundle.message("settings.arthasHome.comment"))
                    .align(AlignX.FILL)
                    .validationOnInput {
                        validateArthasHome(it.text)
                    }
                    .validationOnApply {
                        validateArthasHome(it.text)
                    }
            }

            row(MyBundle.message("settings.arthasAgentPath") + ":") {

                val agentJarFileChooser = FileChooserDescriptorFactory.createSingleFileDescriptor().apply {
                    title = MyBundle.message("settings.arthasAgentPath.chooser")
                    description = MyBundle.message("settings.arthasAgentPath.comment")
                    withFileFilter { file ->
                        file.extension.equals("jar", ignoreCase = true)
                    }
                }

                tfAgentJar = textFieldWithBrowseButton(
                    agentJarFileChooser,
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


            row(MyBundle.message("settings.arthasBootPath") + ":") {

                val bootJarFileChooser = FileChooserDescriptorFactory.createSingleFileDescriptor().apply {
                    title = MyBundle.message("settings.arthasBootPath.chooser")
                    description = MyBundle.message("settings.arthasBootPath.comment")
                    withFileFilter { file ->
                        file.extension.equals("jar", ignoreCase = true)
                    }
                }

                tfBootJar = textFieldWithBrowseButton(
                    bootJarFileChooser,
                    null,
                    fileChosen = { vf -> vf.path }
                )
                    .bindText(settings::arthasBootPath)
                    .comment(MyBundle.message("settings.arthasBootPath.comment"))
                    .align(AlignX.FILL)
                    .validationOnInput {
                        validateJarPath(it.text)
                    }
                    .validationOnApply {
                        validateJarPath(it.text)
                    }
            }


            row(MyBundle.message("settings.arthasCorePath") + ":") {

                val bootJarFileChooser = FileChooserDescriptorFactory.createSingleFileDescriptor().apply {
                    title = MyBundle.message("settings.arthasCorePath.chooser")
                    description = MyBundle.message("settings.arthasCorePath.comment")
                    withFileFilter { file ->
                        file.extension.equals("jar", ignoreCase = true)
                    }
                }

                tfCoreJar = textFieldWithBrowseButton(
                    bootJarFileChooser,
                    null,
                    fileChosen = { vf -> vf.path }
                )
                    .bindText(settings::arthasCorePath)
                    .comment(MyBundle.message("settings.arthasCorePath.comment"))
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

    private fun validateArthasHome(path: String): ValidationInfo? {
        if (path.isBlank()) {
            return null
        }
        val dir = java.io.File(path)
        if (!dir.exists() || !dir.isDirectory) {
            // 文件不存在
            return ValidationInfo(MyBundle.message("file.fileNotExist"))
        }
        return null
    }

    private fun validateJarPath(path: String): ValidationInfo? {
        if (path.isBlank()) {
            return null
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