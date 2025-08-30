package com.github.yuxiaoyao.arthasideahelper.dialog

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.dialog.JvmProcessDialogWrapper.Companion.columnNames
import com.github.yuxiaoyao.arthasideahelper.settings.ArthasAttachSettings
import com.github.yuxiaoyao.arthasideahelper.settings.ArthasParameterState
import com.github.yuxiaoyao.arthasideahelper.utils.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.SearchTextField
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.table.JBTable
import com.sun.tools.attach.VirtualMachine
import com.sun.tools.attach.VirtualMachineDescriptor
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableRowSorter


/**
 * @author kerryzhang on 2025/08/29
 */

private val logger = Logger.getInstance(JvmProcessDialogWrapper::class.java)

class JvmProcessDialogWrapper(private val project: Project) : DialogWrapper(project, true) {

    companion object {
        val columnNames = arrayOf("PID", "Process Name")
    }

    init {
        // must call
        super.init()
    }

    override fun createActions(): Array<out Action?> {
        // remove OK Action
//        return super.createActions()
        return arrayOf(cancelAction)
    }

    override fun createCenterPanel(): JComponent {
        val jdks = JavaUtils.getJdks()
        val projectJdk = JavaUtils.getProjectJdk(project)
        if (projectJdk != null) {
            if (jdks.contains(projectJdk)) {
                jdks.remove(projectJdk)
            }
            jdks.add(0, projectJdk)
        }

        val descriptors = VirtualMachine.list()
        val data = descriptors.filter {
            it.displayName().isNotEmpty()
        }.sortedBy { it.id() }.map { arrayOf(it.id(), it.displayName()) }.toTypedArray()

        return panel {

            lateinit var jdkPathTextField: Cell<TextFieldWithBrowseButton>
            val jdkHomeField = TextFieldWithBrowseButton()
//            row("TEST") {
//                cell(jdkHomeField)
//            }

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
                    .apply {
                        component.text = projectJdk?.homePath ?: ""
                    }
            }

            val tableModel = object : DefaultTableModel(data, columnNames) {
                override fun isCellEditable(row: Int, column: Int): Boolean {
                    // 所有单元格不可编辑
                    return false
                }
            }
            val table = JBTable(tableModel).apply {
                setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
                setShowGrid(false)
            }
            table.preferredScrollableViewportSize = Dimension(520, 320)
            val firstColumn = table.columnModel.getColumn(0)
            firstColumn.minWidth = 60       // 最小宽度
            firstColumn.maxWidth = 60       // 最大宽度
            firstColumn.preferredWidth = 60 // 首选宽度

            // 回车触发 attach
            table.addKeyListener(object : KeyAdapter() {
                override fun keyPressed(e: KeyEvent) {
                    if (e.keyCode == KeyEvent.VK_ENTER) {
                        attachSelected(project, jdkPathTextField.component.text, table, descriptors)
                    }
                }
            })

            // 双击触发 attach
            table.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.clickCount == 2) {
                        attachSelected(project, jdkPathTextField.component.text, table, descriptors)
                    }
                }
            })

            // RowSorter 支持过滤
            val sorter = TableRowSorter(tableModel)
            table.rowSorter = sorter

            // 搜索框
            val searchField = object : SearchTextField() {
                override fun onFieldCleared() {
                    sorter.rowFilter = null
                }
            }

            searchField.addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent) = performFilter(searchField, sorter, tableModel)
                override fun removeUpdate(e: DocumentEvent) = performFilter(searchField, sorter, tableModel)
                override fun changedUpdate(e: DocumentEvent) = performFilter(searchField, sorter, tableModel)
            })

            row {
                // add searchField
                cell(searchField).align(AlignX.FILL).focused()
            }

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

    private fun attachSelected(
        project: Project,
        jdkHome: String,
        table: JBTable,
        descriptors: MutableList<VirtualMachineDescriptor>
    ) {

        val selectedRow = table.selectedRow
        if (selectedRow >= 0) {
            val valueAt = table.model.getValueAt(selectedRow, 0)

            val javaExecutable = JavaUtils.findJavaExecutable(jdkHome)
            if (javaExecutable.isNullOrEmpty()) {
                Messages.showErrorDialog(
                    project,
                    MyBundle.message("java.executableNotFoundDetail", jdkHome),
                    MyBundle.message("java.executableNotFound")
                )
                return
            }
            ArthasTerminalUtils.createBootConsole(project, javaExecutable, valueAt as String)

            close(OK_EXIT_CODE)
//            Messages.showInfoMessage(
//                "SELECTED",
//                "Attach to Process = $valueAt"
//            )

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

}


class JvmProcessDialogWrapper2(private val project: Project) : DialogWrapper(true) {

    private val state = ArthasAttachSettings.getInstance(project).state

    // LEFT
    private lateinit var telnetCheckBox: Cell<JBCheckBox>
    private lateinit var httpCheckBox: Cell<JBCheckBox>
    private lateinit var telnetPortField: Cell<JBTextField>
    private lateinit var httpPortField: Cell<JBTextField>
    private lateinit var ipTextField: Cell<JBTextField>
    private lateinit var sessionTimeoutTextField: Cell<JBTextField>

    private lateinit var tunnelServerEnableCheckBox: Cell<JBCheckBox>
    private lateinit var tunnelServerAppNameTextField: Cell<JBTextField>
    private lateinit var tunnelServerUrlTextField: Cell<JBTextField>
    private lateinit var tunnelServerAgentIdTextField: Cell<JBTextField>

    // RIGHT
    private lateinit var jdkHomeTextField: Cell<TextFieldWithBrowseButton>

    init {
        title = MyBundle.message("attach.attachActionTitle")
        super.init()
    }

    override fun createActions(): Array<out Action?> {
        // remove OK Action
//        return super.createActions()
        return arrayOf(cancelAction)
    }

    override fun createCenterPanel(): JComponent {
        val jdks = JavaUtils.getJdks()
        val projectJdk = JavaUtils.getProjectJdk(project)
        if (projectJdk != null) {
            if (jdks.contains(projectJdk)) {
                jdks.remove(projectJdk)
            }
            jdks.add(0, projectJdk)
        }

        val descriptors = VirtualMachine.list()
        val data = descriptors.filter {
            it.displayName().isNotEmpty()
        }.sortedBy { it.id() }.map { arrayOf(it.id(), it.displayName()) }.toTypedArray()


        val splitter = com.intellij.ui.JBSplitter()
        splitter.proportion = 0.5F

        splitter.firstComponent = panel {
            row(MyBundle.message("arthas.telnetPort") + ":") {
                telnetCheckBox = checkBox("").bindSelected(state::telnetEnable)
                telnetPortField = intTextField(IntRange(1, 65535))
                    .bindIntText(state::telnetPort)
                    .enabledIf(telnetCheckBox.selected)
                button(MyBundle.message("arthas.randomPort")) {
                    generateDataAsync(telnetPortField, NetworkUtil::getAvailableTelnetPort)
                }.enabledIf(telnetCheckBox.selected)

                if (state.telnetEnable) {
                    generateTcpPort(telnetPortField, telnetCheckBox, NetworkUtil::getAvailableTelnetPort)
                }
            }

            row(MyBundle.message("arthas.httpPort") + ":") {
                httpCheckBox = checkBox("").bindSelected(state::httpEnable)
                httpPortField = intTextField(IntRange(1, 65535))
                    .bindIntText(state::httpPort)
                    .enabledIf(httpCheckBox.selected)
                button(MyBundle.message("arthas.randomPort")) {
                    generateDataAsync(httpPortField, NetworkUtil::getAvailableHttPort)
                }.enabledIf(httpCheckBox.selected)
            }

            row(MyBundle.message("arthas.ip") + ":") {
                ipTextField = textField()
                    .bindText(state::ip)
                    .columns(COLUMNS_SHORT)
            }

            row("") {
                button(MyBundle.message("arthas.ipLocal")) {
                    ipTextField.text(SocketUtils.LOCAL_IP)
                }
                button(MyBundle.message("arthas.ipAny")) {
                    ipTextField.text(SocketUtils.ANY_IP)
                }
            }

            row(MyBundle.message("arthas.sessionTimeout") + ":") {
                sessionTimeoutTextField = intTextField(IntRange(1, Int.MAX_VALUE))
                    .bindIntText(state::sessionTimeout)
                    .comment(MyBundle.message("arthas.sessionTimeoutComment"))

                button(MyBundle.message("arthas.sessionTimeoutDefault")) {
                    sessionTimeoutTextField.component.text = "${ArthasUtils.DEFAULT_SESSION_TIMEOUT}"
                }
            }

            separator()

            row {
                tunnelServerEnableCheckBox = checkBox(MyBundle.message("arthas.tunnelEnableConfig"))
            }

            row(MyBundle.message("arthas.tunnelAppName") + ":") {
                tunnelServerAppNameTextField = textField()
                    .bindText(state::appName)
                    .columns(COLUMNS_MEDIUM)
                    .enabledIf(tunnelServerEnableCheckBox.selected)
            }

            row(MyBundle.message("arthas.tunnelServer") + ":") {
                tunnelServerUrlTextField = textField()
                    .bindText(state::tunnelServerUrl)
                    .columns(COLUMNS_MEDIUM)
                    .validationOnInput {
                        validateTunnelServerUrl(it.text)
                    }
                    .enabledIf(tunnelServerEnableCheckBox.selected)
            }

            row("") {
                button(MyBundle.message("arthas.tunnelServerDefault")) {
                    tunnelServerUrlTextField.text(ArthasUtils.DEFAULT_TUNNEL_SERVER)
                }.enabledIf(tunnelServerEnableCheckBox.selected)
            }

            row(MyBundle.message("arthas.tunnelAgentId") + ":") {
                tunnelServerAgentIdTextField = textField()
                    .bindText(state::agentId)
                    .columns(COLUMNS_MEDIUM)
                    .enabledIf(tunnelServerEnableCheckBox.selected)
            }
        }

        // right
        splitter.secondComponent = panel {
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
                            jdkHomeTextField.component.text = selectedSdk?.homePath ?: ""
                        }
                    }
            }

            row("JDK:") {
                val folderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor().apply {
                    title = MyBundle.message("settings.arthasHome")
                }
                jdkHomeTextField = textFieldWithBrowseButton(
                    folderDescriptor,
                    null, fileChosen = { vf ->
                        vf.path
                    })
                    .align(AlignX.FILL)
                    .apply {
                        component.text = projectJdk?.homePath ?: ""
                    }
            }

            val tableModel = object : DefaultTableModel(data, columnNames) {
                override fun isCellEditable(row: Int, column: Int): Boolean {
                    // 所有单元格不可编辑
                    return false
                }
            }
            val table = JBTable(tableModel).apply {
                setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
                setShowGrid(false)
            }
            // table.preferredScrollableViewportSize = Dimension(520, 320)
            val firstColumn = table.columnModel.getColumn(0)
            firstColumn.minWidth = 60       // 最小宽度
            firstColumn.maxWidth = 60       // 最大宽度
            firstColumn.preferredWidth = 60 // 首选宽度

            // 回车触发 attach
            table.addKeyListener(object : KeyAdapter() {
                override fun keyPressed(e: KeyEvent) {
                    if (e.keyCode == KeyEvent.VK_ENTER) {
                        attachSelected(project, jdkHomeTextField.component.text, table)
                    }
                }
            })

            // 双击触发 attach
            table.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.clickCount == 2) {
                        attachSelected(project, jdkHomeTextField.component.text, table)
                    }
                }
            })

            // RowSorter 支持过滤
            val sorter = TableRowSorter(tableModel)
            table.rowSorter = sorter

            // 搜索框
            val searchField = object : SearchTextField() {
                override fun onFieldCleared() {
                    sorter.rowFilter = null
                }
            }

            searchField.addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent) = performFilter(searchField, sorter, tableModel)
                override fun removeUpdate(e: DocumentEvent) = performFilter(searchField, sorter, tableModel)
                override fun changedUpdate(e: DocumentEvent) = performFilter(searchField, sorter, tableModel)
            })

            row {
                // add searchField
                cell(searchField).align(AlignX.FILL).focused()
            }

            row {
                cell(JScrollPane(table)).align(Align.FILL)
            }.resizableRow()
        }

        return panel {
            row {
                cell(splitter).align(Align.FILL)
            }.resizableRow()
        }.apply {
            preferredSize = Dimension(840, 400)
        }
    }

    private fun getCurrentState(): ArthasParameterState {
        val newState = ArthasParameterState(
            telnetEnable = telnetCheckBox.component.isSelected,
            telnetPort = telnetPortField.component.text.toInt(),

            httpEnable = httpCheckBox.component.isSelected,
            httpPort = httpPortField.component.text.toInt(),

            ip = ipTextField.component.text.trim(),

            sessionTimeout = sessionTimeoutTextField.component.text.toInt(),

            tunnelServerEnable = tunnelServerEnableCheckBox.component.isSelected,
        )

        if (newState.telnetEnable) {
            newState.appName = tunnelServerAppNameTextField.component.text.trim()
            val tunnelServerUrl = tunnelServerUrlTextField.component.text.trim()
            if (tunnelServerUrl.isNotEmpty()) {
                newState.tunnelServerUrl = tunnelServerUrl
            }
            newState.agentId = tunnelServerAgentIdTextField.component.text.trim()
        }

        return newState
    }

    private fun attachSelected(project: Project, jdkHome: String, table: JBTable) {
        val javaExecutable = JavaUtils.findJavaExecutable(jdkHome)
        if (javaExecutable.isNullOrEmpty()) {
            Messages.showErrorDialog(
                project,
                MyBundle.message("java.executableNotFoundDetail", jdkHome),
                MyBundle.message("java.executableNotFound")
            )
            return
        }

        val currentState = getCurrentState()
        if (!currentState.telnetEnable && !currentState.httpEnable && !currentState.tunnelServerEnable) {
            // 至少一个启用的
            Messages.showErrorDialog(
                project,
                MyBundle.message("attach.startConfigError"),
                MyBundle.message("pluginName")
            )
            return
        }

        val selectedRow = table.selectedRow
        if (selectedRow >= 0) {
            val pid = table.model.getValueAt(selectedRow, 0)
            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "AsyncTask") {
                override fun run(indicator: ProgressIndicator) {
                    val result = ArthasTerminalUtils.openConsoleByPid(project, javaExecutable, pid, currentState)
                    logger.info("openConsoleByPid. pid = $pid, result = $result")
                    if (result) {
                        // save action
                    }
                }
            })


//            NotificationUtils.showArthasRunningNotification(project)

//            if (settings.telnetEnable) {
//                ArthasTerminalUtils.createBootConsole(project, javaExecutable, valueAt as String)
//            } else {
//                if (settings.httpEnable) {
//                    ArthasTerminalUtils.createBootConsole(project, javaExecutable, valueAt as String)
//                }
//            }
            close(OK_EXIT_CODE)
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


    private fun generateTcpPort(textField: Cell<JBTextField>, checkBox: Cell<JBCheckBox>, provider: () -> Int) {
        // 用 IntelliJ 的后台任务框架，避免阻塞 UI
        ApplicationManager.getApplication().executeOnPooledThread {
            val port = provider.invoke()
            logger.info("生成数据端口. $port")
            SwingUtilities.invokeLater {
                if (port > 0) {
                    textField.text("$port")
                } else {
                    checkBox.selected(false)
                }
            }

//            ApplicationManager.getApplication().invokeLater({
//                telnetPortField.component.text = "$port"
//            }, ModalityState.any())
        }
        /*ProgressManager.getInstance().run {
            val port = -1// provider.invoke()
            ApplicationManager.getApplication().invokeLater {
                if (port == -1) {
                    checkBox.component.isSelected = false
                } else {
//                    state.telnetPort = port
                    textField.text("$port")
                }
            }
        }*/
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

    private fun validateTunnelServerUrl(url: String): ValidationInfo? {
        if (url.isBlank()) {
            return null
        }
        if (url.startsWith("ws://") || url.startsWith("ws://")) {
            return null
        }
        return ValidationInfo(MyBundle.message("url.invalid"))
    }

}