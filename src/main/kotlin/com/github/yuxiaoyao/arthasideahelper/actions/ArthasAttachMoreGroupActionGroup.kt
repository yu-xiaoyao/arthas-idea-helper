package com.github.yuxiaoyao.arthasideahelper.actions

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup


/**
 * @author kerryzhang on 2025/08/26
 */

class ArthasAttachMoreGroupActionGroup :
    DefaultActionGroup(MyBundle.message("attach.arthas.more.jvm"), mutableListOf<AnAction>()) {
    init {
        addAction(object : AnAction("Attach with Telnet") {
            override fun actionPerformed(e: AnActionEvent) {
            }
        })
        addAction(object : AnAction("Attach with PID") {
            override fun actionPerformed(e: AnActionEvent) {
            }
        })
    }
}