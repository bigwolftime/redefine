package com.bigwolftime.util;

import com.bigwolftime.compile.DynamicCompiler;
import com.bigwolftime.constant.JarFileConstant;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

public class ResourceUtils {

    public static String getTomcatLibs(File root) {
        String tomcatLibPath = root.getParentFile().getParentFile().getParentFile().getParent() + "/lib";
        File file = new File(tomcatLibPath);
        if (!file.exists()) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            File[] var4 = Objects.requireNonNull(file.listFiles());

            for (File jar : var4) {
                builder.append(jar.getPath()).append(File.pathSeparator);
            }

            return builder.toString();
        }
    }

    public static String getJars(String jarFile) {
        File file = new File(jarFile);
        if (!file.exists()) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            File[] var3 = Objects.requireNonNull(file.listFiles());

            for (File jar : var3) {
                builder.append(jar.getPath()).append(File.pathSeparator);
            }

            return builder.toString();
        }
    }

    public static String parseFatJarPath(ClassLoader classLoader) {
        // ex: file:/path/projectName/projectName.jar!/BOOT-INF/classes!/
        String rootDir = classLoader.getResource("").getPath();
        // ex: file:/path/projectName/projectName.jar
        rootDir = rootDir.replace("!/" + JarFileConstant.BOOT_INF + "/classes!/", "");
        // ex: /path/projectName/projectName.jar
        rootDir = rootDir.replace("file:", "");

        return rootDir;
    }

}
