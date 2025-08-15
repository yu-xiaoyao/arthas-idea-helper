package com.github.yuxiaoyao.arthasideahelper.settings

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import javax.swing.JCheckBox


/**
 * @author kerryzhang on 2025/08/15
 */

class ArthasHelperProjectConfigurable(private val project: Project) :
    BoundConfigurable(MyBundle.message("arthas.projectSettings")) {

    private val settings = ArthasHelperProjectSettings.getInstance(project)

    override fun createPanel(): DialogPanel {
        return panel {

            row(MyBundle.message("arthas.telnetPort") + ":") {
                val telnetPortField = intTextField(IntRange(-1, 65535))
                    .bindIntText(settings::telnetPort)
                button(MyBundle.message("arthas.disablePort")) {
                    settings.telnetPort = -1
                }
                button(MyBundle.message("arthas.randomPort")) {
                    settings.telnetPort = 0
                }
            }

            row(MyBundle.message("arthas.httpPort") + ":") {
                val httpPortField = intTextField(IntRange(-1, 65535))
                    .bindIntText(settings::httpPort)
                button(MyBundle.message("arthas.disablePort")) {
                    settings.httpPort = -1
                }
                button(MyBundle.message("arthas.randomPort")) {
                    settings.httpPort = 0
                }
            }

            row(MyBundle.message("arthas.ip") + ":") {
                val ipTf = textField()
                    .bindText(settings::ip)
                    .columns(COLUMNS_SHORT)
                button(MyBundle.message("arthas.ipLocal")) {
                    settings.ip = "127.0.0.1"
                }
                button(MyBundle.message("arthas.ipAny")) {
                    settings.ip = "0.0.0.0"
                }
            }

            row(MyBundle.message("arthas.sessionTimeout") + ":") {
                val sessionTimeout = intTextField(IntRange(1, Int.MAX_VALUE))
                    .bindIntText(settings::sessionTimeout)
                    .comment(MyBundle.message("arthas.sessionTimeoutComment"))

                button(MyBundle.message("arthas.sessionTimeoutDefault")) {
//                    sessionTimeout.component.text = "1800"
                    settings.sessionTimeout = 18002
                }
            }

            separator()

            lateinit var enableAdvancedFieldsCheckBox: Cell<JCheckBox>

            row {
                enableAdvancedFieldsCheckBox = checkBox("Enable Advanced Fields")
                    .bindSelected(settings::tunnelEnable)
                    .comment("Enable/disable App Name, Tunnel Server, and Agent ID fields")
            }

            row("App Name:") {
                textField()
                    .bindText(settings::appName)
                    .comment("Application name for Arthas")
                    .columns(COLUMNS_MEDIUM)
                    .enabledIf(enableAdvancedFieldsCheckBox.selected)
            }

            row("Tunnel Server:") {
                textField()
                    .bindText(settings::tunnelServer)
                    .comment("WebSocket tunnel server URL")
                    .columns(COLUMNS_LARGE)
                    .enabledIf(enableAdvancedFieldsCheckBox.selected)
            }

            row("Agent ID:") {
                textField()
                    .bindText(settings::agentId)
                    .comment("Unique agent identifier")
                    .columns(COLUMNS_MEDIUM)
                    .enabledIf(enableAdvancedFieldsCheckBox.selected)
            }
        }
    }
}