//package com.github.yuxiaoyao.arthasideahelper.telnet
//
//import org.apache.commons.net.telnet.TelnetClient
//import java.io.InputStream
//import java.io.OutputStream
//
//
///**
// * @author kerryzhang on 2025/08/24
// */
//
//class TelnetApacheChannel(val telnetClient: TelnetClient) : TelnetChannel {
//
//    override fun close() {
//        telnetClient.disconnect()
//    }
//
//    override fun getInputStream(): InputStream {
//        return telnetClient.inputStream
//    }
//
//    override fun getOutputStream(): OutputStream {
//        return telnetClient.outputStream
//    }
//
//    override fun isClosed(): Boolean {
//        return !telnetClient.isConnected
//    }
//
//    override fun getExitStatus(): Int {
//telnetClient.
//    }
//
//    override fun isConnected(): Boolean {
//        return telnetClient.isConnected()
//    }
//
//
//}