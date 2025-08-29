package com.github.yuxiaoyao.arthasideahelper.dialog

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.utils.ArthasTerminalUtils
import com.github.yuxiaoyao.arthasideahelper.utils.JavaUtils
import com.intellij.codeInspection.options.OptPane.table
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SearchTextField
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.ListTableModel
import com.sun.tools.attach.VirtualMachine
import com.sun.tools.attach.VirtualMachineDescriptor
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.swing.JPanel
import javax.swing.ListSelectionModel
import javax.swing.RowFilter
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableRowSorter


/**
 * @author kerryzhang on 2025/08/26
 */

object JvmProcessDialog {

    val columnNames = arrayOf("PID", "Process Name")

    fun showAttachDialog(project: Project) {

        val descriptors = VirtualMachine.list()
        val data = descriptors.filter {
            it.displayName().isNotEmpty()
        }.sortedBy { it.id() }.map { arrayOf(it.id(), it.displayName()) }.toTypedArray()

        TelnetDialogWrapper(project).show()
    }

    fun showAttachDialog2(project: Project) {
        val descriptors = VirtualMachine.list()
        val data = descriptors.filter {
            it.displayName().isNotEmpty()
        }.sortedBy { it.id() }.map { arrayOf(it.id(), it.displayName()) }.toTypedArray()

        val model = object : DefaultTableModel(data, columnNames) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                // 所有单元格不可编辑
                return false
            }
        }

        // JBTable
        val table = JBTable(model).apply {
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            setShowGrid(false)
//            tableHeader.isVisible = false
//            setRowSelectionInterval(0, 0)
        }

        table.preferredScrollableViewportSize = Dimension(520, 320)

        val firstColumn = table.columnModel.getColumn(0)
        firstColumn.minWidth = 60       // 最小宽度
        firstColumn.maxWidth = 60       // 最大宽度
        firstColumn.preferredWidth = 60 // 首选宽度


        // RowSorter 支持过滤
        val sorter = TableRowSorter(model)
        table.rowSorter = sorter

        // 搜索框
        val searchField = object : SearchTextField() {
            override fun onFieldCleared() {
                sorter.rowFilter = null
            }
        }

        searchField.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = performFilter(searchField, sorter, model)
            override fun removeUpdate(e: DocumentEvent) = performFilter(searchField, sorter, model)
            override fun changedUpdate(e: DocumentEvent) = performFilter(searchField, sorter, model)
        })

        val panel = JPanel(BorderLayout())
        panel.add(searchField, BorderLayout.NORTH)
        panel.add(ScrollPaneFactory.createScrollPane(table), BorderLayout.CENTER)

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, table)
            .setTitle(MyBundle.message("attach.attachActionTitle"))
            .setResizable(true)
            .setMovable(true)
            .setRequestFocus(true)
            .createPopup()

        // 回车触发 attach
        table.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    attachSelected(project, table, descriptors)
                    popup.closeOk(null)
                }
            }
        })

        // 双击触发 attach
        table.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    attachSelected(project, table, descriptors)
                    popup.closeOk(null)
                }
            }
        })


        popup.showInFocusCenter()
        javax.swing.SwingUtilities.invokeLater {
            searchField.textEditor.requestFocusInWindow()
        }
    }

    private fun performFilter(
        searchField: SearchTextField,
        sorter: TableRowSorter<out DefaultTableModel>,
        tableModel: DefaultTableModel
    ) {
        val searchText = searchField.text.trim()

        when {
            searchText.isEmpty() -> sorter.rowFilter = null
            else -> {
                val filters = mutableListOf<RowFilter<Any, Any>>()

                // 为每一列创建过滤器
                repeat(tableModel.columnCount) { columnIndex ->
                    try {
                        val filter = RowFilter.regexFilter<Any, Any>(
                            "(?i).*${Pattern.quote(searchText)}.*",
                            columnIndex
                        )
                        filters.add(filter)
                    } catch (e: PatternSyntaxException) {
                        // 忽略无效的正则表达式
                    }
                }

                // 使用OR逻辑组合所有过滤器
                if (filters.isNotEmpty()) {
                    sorter.rowFilter = RowFilter.orFilter(filters)
                }
            }
        }
    }

    private fun attachSelected(
        project: Project,
        table: JBTable,
        descriptors: MutableList<VirtualMachineDescriptor>
    ) {

        val selectedRow = table.selectedRow
        if (selectedRow >= 0) {
            val valueAt = table.model.getValueAt(selectedRow, 0)

            val javaExecutable = JavaUtils.getJavaExecutable(project) ?: return
            ArthasTerminalUtils.createBootConsole(project, javaExecutable, valueAt as String)

//            Messages.showInfoMessage(
//                "SELECTED",
//                "Attach to Process = $valueAt"
//            )

        }
    }
}