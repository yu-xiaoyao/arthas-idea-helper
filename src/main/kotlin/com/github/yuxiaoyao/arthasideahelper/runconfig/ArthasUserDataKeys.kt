package com.github.yuxiaoyao.arthasideahelper.runconfig

import com.intellij.openapi.util.Key

/**
 * User data keys for storing Arthas configuration in run configurations
 */
object ArthasUserDataKeys {
    val ARTHAS_ENABLED: Key<Boolean> = Key.create("arthas.enabled")
    val ARTHAS_AGENT_PATH: Key<String> = Key.create("arthas.agent.path")
}