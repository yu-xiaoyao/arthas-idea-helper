package com.github.yuxiaoyao.arthasideahelper.utils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * @author Kerry2 on 2025/08/27
 */


object ExecutingCommand {

    /**
     * Executes a command on the native command line and returns the result line by
     * line.
     *
     * @param cmdToRunWithArgs
     * Command to run and args, in an array
     * @return A list of Strings representing the result of the command, or empty
     * string if the command failed
     */
    fun runNative(cmdToRunWithArgs: Array<String>): MutableList<String> {
        var p: Process?
        try {
            p = Runtime.getRuntime().exec(cmdToRunWithArgs)
        } catch (e: SecurityException) {
            return mutableListOf()
        } catch (e: IOException) {
            return mutableListOf()
        }

        val sa = mutableListOf<String>()

        try {
            BufferedReader(InputStreamReader(p.inputStream)).also { reader ->
                var line: String
                while ((reader.readLine().also { line = it }) != null) {
                    sa.add(line)
                }
                p.waitFor()
            }
        } catch (e: IOException) {
            return mutableListOf()
        } catch (ie: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        return sa
    }

}