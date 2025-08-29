package com.github.yuxiaoyao.arthasideahelper.action

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.dialog.AttachWithTelnetDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent


/**
 * @author kerryzhang on 2025/08/29
 */

class AttachWithTelnetAction : AnAction(MyBundle.message("attach.telnetAddress")) {
    override fun actionPerformed(e: AnActionEvent) {
        if (e.project != null) {
            AttachWithTelnetDialog.showAttachDialog(e.project!!)
        }
    }
}