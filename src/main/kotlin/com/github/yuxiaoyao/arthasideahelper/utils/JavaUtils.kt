package com.github.yuxiaoyao.arthasideahelper.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.SystemInfo
import java.io.File


/**
 * @author kerryzhang on 2025/08/27
 */

object JavaUtils {

    fun getJavaExecutable(project: Project): String? {
        val projectSdk = ProjectRootManager.getInstance(project).projectSdk
        if (projectSdk != null) {
            val homePath = projectSdk.homePath
            if (homePath != null) {

                val javaExecutable = if (SystemInfo.isWindows) {
                    File(homePath, "bin/java.exe")
                } else {
                    File(homePath, "bin/java")
                }
                if (javaExecutable.exists()) {
                    return javaExecutable.absolutePath
                }
            }
        }
        return null
    }

}