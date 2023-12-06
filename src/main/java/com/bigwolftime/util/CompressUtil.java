package com.bigwolftime.util;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;

/**
 * @author liuxin
 */
public class CompressUtil {


    public static void extractZip(String fileName, String outputDirectory) {
        try {
            File outFile = new File(outputDirectory);

            File file = new File(fileName);
            InputStream inputStream = Files.newInputStream(file.toPath());


            try (ZipArchiveInputStream in = new ZipArchiveInputStream(inputStream)) {
                ZipArchiveEntry entry;
                while ((entry = in.getNextEntry()) != null) {
                    File outfile = new File(outFile.getCanonicalPath() + File.separator + entry.getName());
                    outfile.getParentFile().mkdirs();

                    if (entry.isDirectory()) {
                        outfile.mkdir();
                        continue;
                    }
                    OutputStream o = new FileOutputStream(outfile);

                    try {
                        IOUtils.copy(in, o);
                    } finally {
                        o.close();
                    }
                }
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
