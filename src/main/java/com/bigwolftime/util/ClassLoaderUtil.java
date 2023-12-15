package com.bigwolftime.util;

import java.lang.instrument.Instrumentation;

/**
 * @author liuxin
 */
public class ClassLoaderUtil {

    /**
     * getClassLoaderByName
     * @param instrumentation instrumentation
     * @param className fully qualified class name
     * @return classLoader
     */
    public static ClassLoader getClassLoaderByName(Instrumentation instrumentation, String className) {
        Class[] classes = instrumentation.getAllLoadedClasses();
        for (Class<?> clazz : classes) {
            if (clazz.getName().equals(className)) {
                return clazz.getClassLoader();
            }
        }

        return null;
    }

}
