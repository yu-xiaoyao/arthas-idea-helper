package com.github.yuxiaoyao.arthasideahelper.dialog

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.utils.JavaUtils
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.panel
import com.sun.tools.attach.VirtualMachine
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.DefaultTableModel


/**
 * @author kerryzhang on 2025/08/29
 */
class TelnetDialogWrapper(private val project: Project) : DialogWrapper(project, true) {

    companion object {
        val columnNames = arrayOf("PID", "Process Name")
    }

    init {
        super.init()
    }

    override fun createActions(): Array<out Action?> {
//        return super.createActions()
        return arrayOf(cancelAction)
    }

    override fun createCenterPanel(): JComponent {
        val jdks = JavaUtils.getJdks()
        val projectJdk = JavaUtils.getProjectJdk(project)
        if (projectJdk != null) {
            if (!jdks.contains(projectJdk)) {
                jdks.add(projectJdk)
            }
        }

        val descriptors = VirtualMachine.list()
        val data = descriptors.filter {
            it.displayName().isNotEmpty()
        }.sortedBy { it.id() }.map { arrayOf(it.id(), it.displayName()) }.toTypedArray()

        return panel {
            lateinit var jdkPathTextField: Cell<TextFieldWithBrowseButton>
            row("SDK:") {
                comboBox(jdks)
                    .align(AlignX.FILL)
                    .apply {
                        if (projectJdk != null) {
                            component.selectedItem = projectJdk
                        }
                        component.renderer = SimpleListCellRenderer.create { label, value, _ ->
                            if (value != null) {
                                label.text = if (value == projectJdk) {
                                    "Project SDK: ${value.name}"
                                } else {
                                    "${value.name} ${value.versionString}"
                                }
                            }
                        }
                        // 添加 ActionListener 来处理选择事件
                        component.addActionListener {
                            val selectedSdk = component.selectedItem as? Sdk
                            jdkPathTextField.component.text = selectedSdk?.homePath ?: ""
                        }
                    }
            }
            row("JDK:") {
                val folderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor().apply {
                    title = MyBundle.message("settings.arthasHome")
                }
                jdkPathTextField = textFieldWithBrowseButton(
                    folderDescriptor,
                    null, fileChosen = { vf ->
                        vf.path
                    })
                    .align(AlignX.FILL)
                    .apply { component.text = projectJdk?.homePath ?: "" }
            }

            val tableModel = object : DefaultTableModel(data, columnNames) {
                override fun isCellEditable(row: Int, column: Int): Boolean {
                    // 所有单元格不可编辑
                    return false
                }
            }
            val table = JTable(tableModel).apply {
                setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
                setShowGrid(false)
            }
            table.preferredScrollableViewportSize = Dimension(520, 320)
            val firstColumn = table.columnModel.getColumn(0)
            firstColumn.minWidth = 60       // 最小宽度
            firstColumn.maxWidth = 60       // 最大宽度
            firstColumn.preferredWidth = 60 // 首选宽度
            row {
                cell(JScrollPane(table))
                    .align(AlignX.FILL)
                    .align(AlignY.FILL)
            }

        }.apply {
            // 设置整个 panel 的首选大小
            // preferredSize = Dimension(600, 400)
        }
    }

}