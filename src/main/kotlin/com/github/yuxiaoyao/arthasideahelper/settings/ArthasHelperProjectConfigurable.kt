package com.github.yuxiaoyao.arthasideahelper.settings

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.utils.ArthasUtils
import com.github.yuxiaoyao.arthasideahelper.utils.NetworkUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*

/**
 * @author kerryzhang on 2025/08/15
 */

class ArthasHelperProjectConfigurable(project: Project) :
    BoundConfigurable(MyBundle.message("arthas.projectSettings")) {

    private val settings = ArthasHelperProjectSettings.getInstance(project)

    override fun createPanel(): DialogPanel {
        return panel {

            row(MyBundle.message("arthas.telnetPort") + ":") {
                val telnetPortField = intTextField(IntRange(-1, 65535))
                    .bindIntText(settings::telnetPort)
                button(MyBundle.message("arthas.disablePort")) {
//                    settings.telnetPort = -1
                    telnetPortField.text("-1")
                }
                button(MyBundle.message("arthas.randomPort")) {
//                    settings.telnetPort = 0
//                    telnetPortField.text("0")
                    generateDataAsync(telnetPortField, NetworkUtil::getAvailableTelnetPort)
                }
            }

            row(MyBundle.message("arthas.httpPort") + ":") {
                val httpPortField = intTextField(IntRange(-1, 65535))
                    .bindIntText(settings::httpPort)
                button(MyBundle.message("arthas.disablePort")) {
//                    settings.httpPort = -1
                    httpPortField.text("-1")
                }
                button(MyBundle.message("arthas.randomPort")) {
//                    settings.httpPort = 0
//                    httpPortField.text("0")
                    generateDataAsync(httpPortField, NetworkUtil::getAvailableHttPort)
                }
            }

            row(MyBundle.message("arthas.ip") + ":") {
                val ipTf = textField()
                    .bindText(settings::ip)
                    .columns(COLUMNS_SHORT)
                button(MyBundle.message("arthas.ipLocal")) {
//                    settings.ip = "127.0.0.1"
                    ipTf.text("127.0.0.1")
                }
                button(MyBundle.message("arthas.ipAny")) {
//                    settings.ip = "0.0.0.0"
                    ipTf.text("0.0.0.0")
                }
            }

            row(MyBundle.message("arthas.sessionTimeout") + ":") {
                val sessionTimeout = intTextField(IntRange(1, Int.MAX_VALUE))
                    .bindIntText(settings::sessionTimeout)
                    .comment(MyBundle.message("arthas.sessionTimeoutComment"))

                button(MyBundle.message("arthas.sessionTimeoutDefault")) {
                    sessionTimeout.component.text = "${ArthasUtils.DEFAULT_SESSION_TIMEOUT}"
                }
            }

            separator()

            lateinit var enableAdvancedFieldsCheckBox: Cell<JBCheckBox>

            row {
                enableAdvancedFieldsCheckBox = checkBox(MyBundle.message("arthas.tunnelEnableConfig"))
                    .bindSelected(settings::tunnelEnable)
            }

            row(MyBundle.message("arthas.tunnelAppName") + ":") {
                textField()
                    .bindText(settings::appName)
                    .columns(COLUMNS_MEDIUM)
                    .enabledIf(enableAdvancedFieldsCheckBox.selected)
            }

            row(MyBundle.message("arthas.tunnelServer") + ":") {
                val tunnelServer = textField()
                    .bindText(settings::tunnelServer)
                    .columns(COLUMNS_MEDIUM)
                    .validationOnInput {
                        validateTunnelServer(it.text)
                    }
                    .enabledIf(enableAdvancedFieldsCheckBox.selected)

                button(MyBundle.message("arthas.tunnelServerDefault")) {
                    tunnelServer.text(ArthasUtils.DEFAULT_TUNNEL_SERVER)
                }.enabledIf(enableAdvancedFieldsCheckBox.selected)
            }

            row(MyBundle.message("arthas.tunnelAgentId") + ":") {
                textField()
                    .bindText(settings::agentId)
                    .columns(COLUMNS_MEDIUM)
                    .enabledIf(enableAdvancedFieldsCheckBox.selected)
            }
        }
    }

    private fun generateDataAsync(textField: Cell<JBTextField>, provider: () -> Int) {
        // 用 IntelliJ 的后台任务框架，避免阻塞 UI
        ProgressManager.getInstance().run {
            val port = provider.invoke()
            ApplicationManager.getApplication().invokeLater {
                textField.text("$port")
            }
        }
    }

    private fun validateTunnelServer(url: String): ValidationInfo? {
        if (url.isBlank()) {
            return null
        }
        if (url.startsWith("ws://") || url.startsWith("ws://")) {
            return null
        }
        return ValidationInfo(MyBundle.message("url.invalid"))
    }

}