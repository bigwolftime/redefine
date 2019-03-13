package com.bigwolftime.util;

import java.io.File;
import java.util.Objects;

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

}
