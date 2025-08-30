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

    fun getJdks(): MutableList<Sdk> {
        return ProjectJdkTable.getInstance().allJdks.filter { it.sdkType is JavaSdk }.toMutableList()
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

    fun findJavaExecutable(homePath: String?): String? {
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
        return null
    }

    fun getJavaExecutable(sdk: Sdk?): String? {
        if (sdk != null) {
            return findJavaExecutable(sdk.homePath)
        }
        return null
    }


    fun getJavaExecutable(project: Project): String? {
        return getJavaExecutable(ProjectRootManager.getInstance(project).projectSdk)
    }

}