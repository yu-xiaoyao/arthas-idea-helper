package com.github.yuxiaoyao.arthasideahelper.actions

import com.github.yuxiaoyao.arthasideahelper.MyBundle
import com.github.yuxiaoyao.arthasideahelper.action.AttachWithPidAction
import com.github.yuxiaoyao.arthasideahelper.action.AttachWithTelnetAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup


/**
 * @author kerryzhang on 2025/08/26
 */

class ArthasAttachMoreGroupActionGroup :
    DefaultActionGroup(MyBundle.message("attach.arthas.more.jvm"), mutableListOf<AnAction>()) {
    init {
//        addAction(object : AnAction(MyBundle.message("attach.telnetAddress")) {
//            override fun actionPerformed(e: AnActionEvent) {
//            }
//        })
//        addAction(object : AnAction(MyBundle.message("attach.PID")) {
//            override fun actionPerformed(e: AnActionEvent) {
//            }
//        })
        addAction(AttachWithTelnetAction())
        addAction(AttachWithPidAction())
    }
}