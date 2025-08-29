package com.github.yuxiaoyao.arthasideahelper.action

import com.github.yuxiaoyao.arthasideahelper.utils.NotificationUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.projectRoots.ProjectJdkTable


/**
 * @author Kerry2 on 2025/08/26
 */

private val logger = Logger.getInstance(ArthasTestAction::class.java)

class ArthasTestAction : AnAction("Test Arthas") {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        if (project == null) {
            return
        }
        NotificationUtils.sendNotification(project, "Test Arthas Test action")

//        testJdkList(project)
        // SocketUtils.findTcpListenProcess(3658)


//        val telnetClient = TelnetUtils.createTelnetClient("127.0.0.1", 3658) ?: return
//        val process = ArthasRemoteTelnetProcess(telnetClient)
//        ToolWindowsUtils.addArthasConsole(
//            project,
//            "Test ${Random.nextInt(10000)}",
//            ArthasColoredRemoteProcessHandler(process)
//        )
//

//        val telnetClient = TelnetUtils.createTelnetClient("127.0.0.1", 3658)
//        if (telnetClient == null) {
//            return
//        }
//        val tabId = "127.0.0.1:3658"
//        val processHandler = ArthasProcessManager.createProcessHandler(tabId, ArthasRemoteTelnetProcess(telnetClient))
//        ToolWindowsUtils.addArthasConsole(project, tabId, processHandler, forceAdd = true)
    }

    private fun testJdkList(project: Project) {
        val jdks = ProjectJdkTable.getInstance().allJdks.toList().filter { it.sdkType is JavaSdk }

        jdks.forEach {
            logger.info("Test JDK: $it")
            logger.info("Test JDK info: ${it.name} - ${it.sdkType} - ${it.homePath} - ${it.homeDirectory}")
        }

    }
}