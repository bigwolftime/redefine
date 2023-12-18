package com.bigwolftime.api;

import com.bigwolftime.compile.DynamicCompiler;
import com.bigwolftime.util.ClassLoaderUtil;
import com.bigwolftime.util.IOUtil;
import net.bytebuddy.agent.ByteBuddyAgent;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@WebServlet("/backend/redefine/base64")
@MultipartConfig
public class ExtendRedefineServlet extends HttpServlet {

    private static final Instrumentation INSTRUMENT;

    static {
        INSTRUMENT = ByteBuddyAgent.install();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        String cls = req.getParameter("class");
        String file = req.getParameter("base64_file");

        byte[] decode = Base64.getDecoder().decode(file);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decode);

        byte[] bytes = compileByArthas(byteArrayInputStream, cls);
        try {
            transformer(cls, bytes);
            print(resp, "redefine success.");
        } catch (ClassNotFoundException | UnmodifiableClassException e) {
            print(resp, "transform error. " + e.getMessage());
        }
    }

    private byte[] compileByArthas(InputStream inputStream, String className) throws IOException {
        String jarPath = DynamicCompiler.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File file = new File(jarPath);

        ClassLoader classLoader = ClassLoaderUtil.getClassLoaderByName(INSTRUMENT, className);
        if (Objects.isNull(classLoader)) {
            // default system class loader
            classLoader = new URLClassLoader(new URL[] { file.toURI().toURL() }, ClassLoader.getSystemClassLoader().getParent());
        }

        DynamicCompiler dynamicCompiler = new DynamicCompiler(classLoader);

        String code = IOUtil.toString(inputStream);
        dynamicCompiler.addSource(className, code);

        Map<String, byte[]> byteCodes = dynamicCompiler.buildByteCodes();

        Iterator<byte[]> iterator = byteCodes.values().stream().iterator();
        return iterator.next();
    }

    private void transformer(String classNameWithPoint, byte[] bytes) throws ClassNotFoundException, UnmodifiableClassException {
        String classNameWithSlant = classNameWithPoint.replaceAll("\\.", "/");

        ClassFileTransformer transformer = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (className.equals(classNameWithSlant)) {
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
