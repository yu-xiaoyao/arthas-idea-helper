package com.github.yuxiaoyao.arthasideahelper.actions

import com.github.yuxiaoyao.arthasideahelper.utils.ArthasUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

/**
 * Arthas 附加动作示例
 * @author kerryzhang on 2025/08/14
 */
class ArthasAttachAction : AnAction("Attach Arthas") {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        // 验证 Arthas Agent 路径
        if (!ArthasUtils.validateArthasAgentPath(project)) {
            return
        }
        
        // 获取 Arthas Agent 路径并执行相关操作
        val agentPath = ArthasUtils.getArthasAgentPath()
        val jvmArgs = ArthasUtils.getArthasAgentJvmArgs()
        
        Messages.showInfoMessage(
            project,
            "Arthas Agent 路径: $agentPath\nJVM 参数: $jvmArgs",
            "Arthas 配置信息"
        )
        
        // 这里可以添加实际的 Arthas 附加逻辑
        // 例如：启动 Java 进程时添加 javaagent 参数
    }
    
    override fun update(e: AnActionEvent) {
        // 只在有项目时启用此动作
        e.presentation.isEnabled = e.project != null
    }
}