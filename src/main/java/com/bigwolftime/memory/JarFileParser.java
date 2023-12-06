package com.bigwolftime.memory;

import com.bigwolftime.compile.DynamicCompiler;
import com.bigwolftime.constant.JarFileConstant;
import com.bigwolftime.util.CompressUtil;
import com.bigwolftime.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuxin
 *
 * Locate the fatJar package and extract it to /tmp floder,
 * then collect the list of jar in these dirctory: BOOT-INF/classes, BOOT-INF/lib
 */
public class JarFileParser {

    private volatile static JarFileParser jarFileParser;


    /**
     * jar name list: BOOT-INF/classes
     */
    private final List<String> classesJarsList;

    /**
     * jar name list: BOOT-INF/lib
     */
    private final List<String> libJarsList;

    private JarFileParser() throws FileNotFoundException {
        // parse fat jar path
        String rootDir = ResourceUtils.parseFatJarPath(DynamicCompiler.class.getClassLoader());
        // uncompress
        CompressUtil.extractZip(rootDir, JarFileConstant.UNCOMPRESS_PATH);

        String classesDir = JarFileConstant.UNCOMPRESS_PATH + JarFileConstant.BOOT_INF + JarFileConstant.CLASSES;
        String libDir = JarFileConstant.UNCOMPRESS_PATH + JarFileConstant.BOOT_INF + JarFileConstant.LIB;

        File classesFilePath = new File(classesDir);
        File libFilePath = new File(libDir);

        File[] classesPathListFileArr = classesFilePath.listFiles();
        File[] libPathListFileArr = libFilePath.listFiles();

        if (classesPathListFileArr == null || libPathListFileArr == null) {
            throw new FileNotFoundException("classesFileArr or libFileArr not found!");
        }

        classesJarsList = Arrays.stream(classesPathListFileArr).map(File::getAbsoluteFile).map(File::getAbsolutePath).collect(Collectors.toList());
        libJarsList = Arrays.stream(libPathListFileArr).map(File::getAbsoluteFile).map(File::getAbsolutePath).collect(Collectors.toList());
    }


    /**
     * get instance with double check
     * @return
     */
    public static JarFileParser getJarFileParser() throws FileNotFoundException {
        if (jarFileParser == null) {
            synchronized (JarFileParser.class) {
                if (jarFileParser == null) {
                    jarFileParser = new JarFileParser();
                }
            }
        }
        return jarFileParser;
    }


    public List<String> getClassesJarsList() {
        return classesJarsList;
    }

    public String getClassesJarsStr() {
        StringBuilder sb = new StringBuilder();
        for (String classesJar : classesJarsList) {
            sb.append(classesJar).append(File.pathSeparator);
        }

        return sb.toString();
    }


    public List<String> getLibJarsList() {
        return libJarsList;
    }

    public String getLibJarStr() {
        StringBuilder sb = new StringBuilder();
        for (String libJar : libJarsList) {
            sb.append(libJar).append(File.pathSeparator);
        }

        return sb.toString();
    }

}
