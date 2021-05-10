Use ByteBuddy and Instrument API for code redefinition(Java)

对 Arthas Redefine 功能的封装，通过 API 接口的方式上传修改后的 Java 文件，并自动编译，替换

#### 使用方法

[Maven Central Repository](https://search.maven.org/artifact/io.github.bigwolftime/redefine) 

```xml
<dependency>
  <groupId>io.github.bigwolftime</groupId>
  <artifactId>redefine</artifactId>
  <version>0.0.2</version>
</dependency>
```

以 `Postman` 为例：

* 地址：`projectDomain/backend/redefine`
* 请求方式：`POST form-data`
* 参数

| 参数名 | 示例                              | 备注                                         |
| ------ | --------------------------------- | -------------------------------------------- |
| class  | com.xx.yy.YourClass               | 要替换的全限定类名(即 package.YourClassName) |
| file   | /User/project/com/xx/yy/YourClass.java | 文件的路径                                   |


以 `curl` 方式请求：

`curl --location --request POST 'https://domain/projectName/backend/redefine' \
--form 'class="com.xx.yy.YourClass"' \
--form 'file=@"/User/project/com/xx/yy/YourClass.java"'`


#### 注意事项

1. 由于 JDK 限制，替换的新类对于原来的类，不能修改方法签名，不能新增/减少方法，否则会抛出异常 `Attempted to add/delete a method`；
   不能新增/删除/修改类级别的成员变量，否则会抛出异常 `attempted to change the schema (add/remove fields)`；不能修改枚举类的内容
2. 向外暴露接口，需要考虑权限校验，项目中并没有相关实现

#### 已知问题

1. 某些低版本 Tomcat 在应用启动时会报错:

```java
SEVERE: Unable to process Jar entry [META-INF/versions/9/module-info.class] from Jar [jar:file:/WEB-INF/lib/byte-buddy-agent-1.9.3.jar!/] for annotations
org.apache.tomcat.util.bcel.classfile.ClassFormatException: Invalid byte tag in constant pool: 19
	at org.apache.tomcat.util.bcel.classfile.Constant.readConstant(Constant.java:136)
	at org.apache.tomcat.util.bcel.classfile.ConstantPool.<init>(ConstantPool.java:59)
	at org.apache.tomcat.util.bcel.classfile.ClassParser.readConstantPool(ClassParser.java:208)
	at org.apache.tomcat.util.bcel.classfile.ClassParser.parse(ClassParser.java:118)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsStream(ContextConfig.java:2055)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsJar(ContextConfig.java:1931)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsUrl(ContextConfig.java:1897)
	at org.apache.catalina.startup.ContextConfig.processAnnotations(ContextConfig.java:1882)
	at org.apache.catalina.startup.ContextConfig.webConfig(ContextConfig.java:1314)
	at org.apache.catalina.startup.ContextConfig.configureStart(ContextConfig.java:873)
	at org.apache.catalina.startup.ContextConfig.lifecycleEvent(ContextConfig.java:371)
	at org.apache.catalina.util.LifecycleSupport.fireLifecycleEvent(LifecycleSupport.java:117)
	at org.apache.catalina.util.LifecycleBase.fireLifecycleEvent(LifecycleBase.java:90)
	at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:5355)
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:150)
	at org.apache.catalina.core.ContainerBase.addChildInternal(ContainerBase.java:901)
	at org.apache.catalina.core.ContainerBase.addChild(ContainerBase.java:877)
	at org.apache.catalina.core.StandardHost.addChild(StandardHost.java:632)
	at org.apache.catalina.startup.HostConfig.deployWAR(HostConfig.java:1073)
	at org.apache.catalina.startup.HostConfig$DeployWar.run(HostConfig.java:1857)
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
```

可忽略. 其原因是自 Java9 引入了**模块化**, 且新版本的 ByteBuddy 使用了此特性，导致某些低版本 Tomcat 无法处理. 具体可见
   [此处1](https://stackoverflow.com/questions/53063324/severe-unable-to-process-jar-entry-module-info-class-in-tomcat-7-java-8)
   [此处2](https://github.com/classgraph/classgraph/issues/291#issuecomment-446493724)

---

MyNote:

1. GPG keys upload
   https://central.sonatype.org/pages/working-with-pgp-signatures.html#installing-gnupg

Server List   
* hkp://keys.gnupg.net
* hkp://pool.sks-keyservers.net


2. mvn deploy occur: gpg: signing failed: Inappropriate ioctl for device

原因是 gpg 在当前终端无法弹出密码输入页面
`export GPG_TTY=$(tty)`
~/.bash_profile

reference: https://my.oschina.net/ujjboy/blog/3023151