package com.lucky.platform.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author: Loki
 * @data: 2022-05-12 17:59
 */
public class ClassLoaderUtil {
    public static void main(String[] args) {
        Path path = Paths.get("");
        buildClassLoader(path);
    }

    public static ClassLoader buildClassLoader(Path path){
        try {
            final URL[] urls = Files.list(path)
                    .filter(each -> each.getFileName().toString().endsWith(".jar"))
                    .map(ClassLoaderUtil::pathToURL)
                    .toArray(URL[]::new);

            return URLClassLoader.newInstance(urls, ClassLoaderUtil.class.getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list jars in [" + path + "]: " + e.getMessage(), e);
        }
    }

    private static URL pathToURL(Path path) {
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
            // Shouldn't happen, but have to handle the exception
            throw new RuntimeException("Failed to convert path [" + path + "] to URL", e);
        }
    }
}
