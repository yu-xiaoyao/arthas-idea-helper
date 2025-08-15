
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