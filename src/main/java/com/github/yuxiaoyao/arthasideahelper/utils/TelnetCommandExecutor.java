package com.github.yuxiaoyao.arthasideahelper.utils;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * IDEA插件中的Telnet命令执行器
 * 支持连接到telnet服务器并执行命令
 */
public class TelnetCommandExecutor {

    private ProcessHandler processHandler;
    private OutputStreamWriter commandWriter;
    private final Project project;

    public TelnetCommandExecutor(Project project) {
        this.project = project;
    }

    /**
     * 连接到telnet服务器
     */
    public CompletableFuture<Boolean> connectToTelnet(String host, int port) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                // 创建telnet命令
                GeneralCommandLine commandLine = new GeneralCommandLine();
                commandLine.setExePath("telnet");
                commandLine.addParameter(host);
                commandLine.addParameter(String.valueOf(port));

                // 创建进程处理器
                processHandler = new OSProcessHandler(commandLine);

                // 添加进程监听器
                processHandler.addProcessListener(new ProcessListener() {
                    @Override
                    public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                        String text = event.getText();
                        // 在IDEA中显示输出
                        ApplicationManager.getApplication().invokeLater(() -> {
                            showOutput(text, outputType);
                        });
                    }

                    @Override
                    public void processTerminated(@NotNull ProcessEvent event) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            showOutput("Telnet连接已断开", ProcessOutputTypes.SYSTEM);
                        });
                    }
                });

                // 启动进程
                processHandler.startNotify();


                // 获取输入流用于发送命令
                commandWriter = new OutputStreamWriter(
                        processHandler.getProcessInput(),
                        StandardCharsets.UTF_8
                );

                future.complete(true);

            } catch (Exception e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    Messages.showErrorDialog(project,
                            "连接telnet失败: " + e.getMessage(),
                            "Telnet连接错误");
                });
                future.complete(false);
            }
        });

        return future;
    }

    /**
     * 向telnet服务器发送命令
     */
    public void sendCommand(String command) {
        if (commandWriter == null || processHandler == null || processHandler.isProcessTerminated()) {
            Messages.showWarningDialog(project,
                    "请先连接到telnet服务器",
                    "未连接");
            return;
        }

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                commandWriter.write(command + "\n");
                commandWriter.flush();
            } catch (IOException e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    Messages.showErrorDialog(project,
                            "发送命令失败: " + e.getMessage(),
                            "命令发送错误");
                });
            }
        });
    }

    /**
     * 断开telnet连接
     */
    public void disconnect() {
        if (processHandler != null && !processHandler.isProcessTerminated()) {
            try {
                if (commandWriter != null) {
                    commandWriter.write("quit\n");
                    commandWriter.flush();
                    commandWriter.close();
                }
                processHandler.destroyProcess();
            } catch (IOException e) {
                // 忽略关闭时的异常
            }
        }
    }

    /**
     * 在IDEA中显示telnet输出
     */
    private void showOutput(String text, Key outputType) {
        // 方法1: 使用Console输出
        showInConsole(text, outputType);

        // 方法2: 也可以显示在Event Log中
        // showInEventLog(text);
    }

    /**
     * 在Console中显示输出
     */
    private void showInConsole(String text, Key outputType) {
        // 获取或创建Console
        ConsoleView console = getOrCreateConsole();
        if (console != null) {
            if (outputType == ProcessOutputTypes.STDERR) {
                console.print(text, ConsoleViewContentType.ERROR_OUTPUT);
            } else if (outputType == ProcessOutputTypes.SYSTEM) {
                console.print(text, ConsoleViewContentType.SYSTEM_OUTPUT);
            } else {
                console.print(text, ConsoleViewContentType.NORMAL_OUTPUT);
            }
        }
    }

    /**
     * 获取或创建Console视图
     */
    private ConsoleView getOrCreateConsole() {
        // 这里需要根据你的插件设计来实现
        // 可以使用TextConsoleBuilderFactory创建console
        TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        return builder.getConsole();
    }

    /**
     * 批量执行多个telnet命令
     */
    public void executeCommandSequence(String host, int port, String... commands) {
        connectToTelnet(host, port).thenAccept(connected -> {
            if (connected) {
                // 等待连接稳定后执行命令序列
                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    try {
                        Thread.sleep(1000); // 等待连接建立
                        for (String command : commands) {
                            sendCommand(command);
                            Thread.sleep(500); // 命令间隔
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        });
    }
}
