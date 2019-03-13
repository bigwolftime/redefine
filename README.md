> 通过动态编译 Java 类生成 class 字节码执行，避免每次新加Test接口，都要提交打包发布等流程。

#### Dependency

```
<dependency>
    <groupId>suishen</groupId>
    <artifactId>dynamic-compile</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### Use

- Callable 形式
```
public class TestAction implements Callable<String> {
    @Override
    public String call() throws Exception {
        return "hello world";
    }
}
```

- BiFunction 形式
```
public class TestAction implements BiFunction<HttpServletRequest, HttpServletResponse, String> {
    @Override
    public String apply(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        return "hello world";
    }
}
```

将要执行的代码以接口形式提交
- code 代码
- cls 全限类名

```
@PostMapping("/call")
public JSONResult compileExecute(@RequestParam("code") String code,
                                @RequestParam("class") String cls) {
    try {
        Object result = DynamicCompile.compileCall(code, cls);
        return JSONResult.okResult(result);
    } catch (Throwable e) {
        SLogger.error(e.getMessage(), e);
        return JSONResult.failureResult(e.getMessage());
    }
}

@PostMapping("/apply")
public JSONResult compileExecute(@RequestParam("code") String code,
                             @RequestParam("class") String cls,
                             HttpServletRequest request,
                             HttpServletResponse response) {
    try {
        Object result = DynamicCompile.compileApply(code, cls, request, response);
        return JSONResult.okResult(result);
    } catch (Throwable e) {
        SLogger.error(e.getMessage(), e);
        return JSONResult.failureResult(e.getMessage());
    }
}
```
