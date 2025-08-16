## Arthas 参数

### agent 调用参数

```shell
-javaagent:D:\arthas-agent.jar=C:/arthas/arthas-core.jar;telnetPort=0,httpPort=0,appName=test,processId=12345
```

> `=` 后面第一个必须是 `arthas-core.jar`的路径, 使用 `;` 分割.

如果为空, 则 使用 `;` 作为开始符号

```shell
-javaagent:D:\arthas-agent.jar=;telnetPort=0,httpPort=0,appName=test,processId=12345
```

## 通知

### plugin.xml

```xml

<notificationGroup displayType="BALLOON"
                   id="com.github.yuxiaoyao.arthasideahelper.notification"
                   isLogByDefault="true"
                   toolWindowId="Event Log"
                   bundle="messages.MyBundle"
                   key="notificationGroupId"/>
```

**displayType**:

- BALLOON: 目测右下角弹出来(默认)

**isLogByDefault**:

- true: 在通知组中显示并记录
- false: 会显示, 但不会记录
