package com.github.yuxiaoyao.arthasideahelper.action

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.dialog.JvmProcessDialog
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent


/**
 * @author kerryzhang on 2025/08/18
 */


class AttachArthasAction : AnAction(MyBundle.message("attach.arthas.jvm"), null, AllIcons.Actions.Attach) {
    override fun actionPerformed(event: AnActionEvent) {
        if (event.project != null) {
            JvmProcessDialog.showAttachDialog(event.project!!)
        }
    }
}