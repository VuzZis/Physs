package com.skoow.physs.util;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class FileClassCompiler {
    public static Class<?> compile(String className, File searchDir, File javaSource) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Java compiler is not available.");
        }
        int result = compiler.run(null, null, null, javaSource.getPath());
        if (result != 0) {
            throw new IOException("Compilation failed");
        }
        URLClassLoader classLoader = new URLClassLoader(new URL[] { searchDir.toURI().toURL() });
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IOException("Class not found after compilation", e);
        }
    }
}
