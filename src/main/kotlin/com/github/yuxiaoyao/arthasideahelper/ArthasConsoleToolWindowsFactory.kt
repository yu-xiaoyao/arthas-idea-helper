package com.github.yuxiaoyao.arthasideahelper

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.*


/**
 * @author kerryzhang on 2025/08/26
 */

class ArthasConsoleToolWindowsFactory : ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        val ui = panel {
            group("User Info") {
                row("Name:") {
                    textField()
                        .bindText({ "逍遥" }, { _ -> })
                        .columns(20)
                }
                row("Age:") {
                    intTextField(0..120)
                        .bindIntText({ 25 }, { _ -> })
                }
            }

            group("Settings") {
                row {
                    checkBox("Enable feature A")
                        .bindSelected({ true }, { _ -> })
                }
                row {
                    checkBox("Enable feature B")
                        .bindSelected({ false }, { _ -> })
                }
            }

            group("Actions") {
                row {
                    button("Say Hello") {
                        println("Hello from DSL UI!")
                    }
                }
            }
        }

        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(ui, "", false)
        toolWindow.contentManager.addContent(content)
    }
}