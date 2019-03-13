package suishen.compile;

import org.apache.commons.lang3.StringUtils;
import suishen.libs.log.SLogger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;

/**
 * @author zhuangchao
 * @date 2018/12/6 10:11
 */
public class DynamicCompile {

    public static Object compileCall(String code, String classFullName) throws Throwable {
        validate(code, classFullName);
        String root = DynamicCompile.class.getClassLoader().getResource("").getPath();
        SLogger.info("root=" + root);
        String jarFile = root.replace("/classes", "/lib");
        SLogger.info("jarFile=" + jarFile);

        Class<?> cls = ClassGenerator.generate(classFullName, code, root, jarFile);
        if (cls == null) {
            throw new RuntimeException("compile error");
        }
        Object instance = cls.newInstance();
        if (!(instance instanceof Callable)) {
            throw new RuntimeException("only support Callable interface");
        }

        MethodType methodType = MethodType.methodType(Object.class);
        MethodHandle methodHandle = MethodHandles.lookup().findVirtual(cls, "call", methodType);
        return methodHandle.invoke(instance);
    }

    public static <T, U> Object compileApply(String code, String classFullName, T t, U u) throws Throwable {
        validate(code, classFullName);
        String root = DynamicCompile.class.getClassLoader().getResource("").getPath();
        SLogger.info("root=" + root);
        String jarFile = root.replace("/classes", "/lib");
        SLogger.info("jarFile=" + jarFile);

        Class<?> cls = ClassGenerator.generate(classFullName, code, root, jarFile);
        if (cls == null) {
            throw new RuntimeException("compile error");
        }
        Object instance = cls.newInstance();
        if (!(instance instanceof BiFunction)) {
            throw new RuntimeException("only support BiFunction interface");
        }

        MethodType methodType = MethodType.methodType(Object.class, Object.class, Object.class);
        MethodHandle methodHandle = MethodHandles.lookup().findVirtual(cls, "apply", methodType);
        return methodHandle.invoke(instance, t, u);
    }


    private static void validate(String code, String cls) {
        if (StringUtils.isBlank(code)) {
            throw new RuntimeException("code is null or empty");
        }
        if (StringUtils.isBlank(cls)) {
            throw new RuntimeException("class is null or empty");
        }
    }
}
