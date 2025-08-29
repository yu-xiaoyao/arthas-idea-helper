package com.github.yuxiaoyao.arthasideahelper.action

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.dialog.AttachWithPidDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project


/**
 * @author kerryzhang on 2025/08/29
 */

class AttachWithPidAction : AnAction(MyBundle.message("attach.PID")) {
    override fun actionPerformed(e: AnActionEvent) {
        if (e.project != null) {
            AttachWithPidDialog.showAttachDialog(e.project!!)
        }
    }
}
