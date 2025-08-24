//package com.github.yuxiaoyao.arthasideahelper.telnet
//
//import java.io.Closeable
//import java.io.InputStream
//import java.io.OutputStream
//
//
///**
// * @author kerryzhang on 2025/08/24
// */
//
//interface TelnetChannel : Closeable {
//
//    override fun close()
//
//    fun getInputStream(): InputStream
//
//    fun getOutputStream(): OutputStream
//
//    fun isClosed(): Boolean
//
//    fun getExitStatus(): Int
//
//    fun isConnected(): Boolean
//
//}