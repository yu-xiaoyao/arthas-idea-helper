package com.github.yuxiaoyao.arthasideahelper.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.SystemInfo
import java.io.File


/**
 * @author kerryzhang on 2025/08/27
 */

object JavaUtils {

    fun getJdks(): List<Sdk> {
        return ProjectJdkTable.getInstance().allJdks.toList().filter { it.sdkType is JavaSdk }
    }

    fun getProjectJdk(project: Project): Sdk? {
        val projectSdk = ProjectRootManager.getInstance(project).projectSdk
        if (projectSdk != null) {
            if (projectSdk.sdkType is JavaSdk) {
                return projectSdk
            }
        }
        return null
    }


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