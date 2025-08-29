package com.github.yuxiaoyao.arthasideahelper.dialog

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.utils.JavaUtils
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent


/**
 * @author kerryzhang on 2025/08/29
 */
class TelnetDialogWrapper(private val project: Project) : DialogWrapper(project, true) {

    override fun createCenterPanel(): JComponent {
        val jdks = JavaUtils.getJdks()
        val projectJdk = JavaUtils.getProjectJdk(project)

        val panel = panel {
            row("SDK:") {
                comboBox(jdks)
            }
            row("JDK:") {
                textField()
            }
        }
        return panel
    }

}

object AttachWithTelnetDialog {

    fun showAttachDialog(project: Project) {

//        TelnetDialogWrapper(project).show()

        val jdks = JavaUtils.getJdks()
        val projectJdk = JavaUtils.getProjectJdk(project)

        val panel = panel {
            row("SDK:") {
                comboBox(jdks)
                    .component.renderer = SimpleListCellRenderer.create { label, value, _ ->
                    label.text = value.name + " " + value.versionString
                }
            }
        }

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, null)
            .setTitle(MyBundle.message("attach.attachActionTitle"))
            .setResizable(true)
            .setMovable(true)
            .setRequestFocus(true)
            .createPopup()
        popup.showInFocusCenter()
    }

}