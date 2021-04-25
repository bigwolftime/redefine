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