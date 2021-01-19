package com.bigwolftime.api;

import com.bigwolftime.compile.DynamicCompile;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

@WebServlet("/backend/redefine")
@MultipartConfig
public class ExtendRedefineServlet extends HttpServlet {

    private static final Instrumentation INSTRUMENT;

    static {
        INSTRUMENT = ByteBuddyAgent.install();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        String cls = req.getParameter("class");

        Part filePart = req.getPart("file");

        byte[] bytes = compileByArthas(filePart.getInputStream(), cls);
        try {
            transformer(cls, bytes);
            print(resp, "redefine success.");
        } catch (ClassNotFoundException | UnmodifiableClassException e) {
            print(resp, "transform error. " + e.getMessage());
        }
    }

    private byte[] compileByArthas(InputStream inputStream, String className) throws IOException {
        String jarPath = DynamicCompile.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File file = new File(jarPath);

        URLClassLoader classLoader = new URLClassLoader(new URL[] { file.toURI().toURL() },
                ClassLoader.getSystemClassLoader().getParent());

        DynamicCompile dynamicCompiler = new DynamicCompile(classLoader);

        String code = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        dynamicCompiler.addSource(className, code);

        Map<String, byte[]> byteCodes = dynamicCompiler.buildByteCodes();

        Iterator<byte[]> iterator = byteCodes.values().stream().iterator();
        return iterator.next();
    }

    private void transformer(String classNameWithPoint, byte[] bytes) throws ClassNotFoundException, UnmodifiableClassException {
        String classNameWithSlant = classNameWithPoint.replaceAll("\\.", "/");

        ClassFileTransformer transformer = (loader, innerClassName, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (innerClassName.equals(classNameWithSlant)) {
                return bytes;
            }

            return null;
        };

        INSTRUMENT.addTransformer(transformer, true);
        INSTRUMENT.retransformClasses(Class.forName(classNameWithPoint));
    }

    private void print(HttpServletResponse response, String result) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(result);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

}
