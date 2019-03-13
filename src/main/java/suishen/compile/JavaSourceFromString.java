package suishen.compile;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * @author zhuangchao
 * @date 2018/12/6 10:08
 */
public class JavaSourceFromString extends SimpleJavaFileObject {

    private String code;

    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }

}