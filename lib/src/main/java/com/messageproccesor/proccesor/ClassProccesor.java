package com.messageproccesor.proccesor;

import com.messageproccesor.utils.LoggerMessageProccesor;
import com.messageproccesor.utils.StandardSystemProperty;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
public class ClassProccesor {

    private static final String CLASS_FILE_NAME_EXTENSION = ".class";
    private Set<Location> resource;

    protected ClassProccesor(Set<Location> resource){
        this.resource=resource;
    }

    public static ClassProccesor from(ClassLoader classLoader){
        Set<Location> locations=getClassPath(classLoader)
                .entrySet().stream()
                .map(entrySet->new Location(entrySet.getKey(),entrySet.getValue()))
                .collect(Collectors.toSet());

        return new ClassProccesor(locations);
    }
    private static Map<File,ClassLoader> getClassPath(ClassLoader classLoader){
        Map<File,ClassLoader> fileClassLoaderHashMap=new HashMap<>();

        ClassLoader parent=classLoader.getParent();
        if(parent!=null)
            fileClassLoaderHashMap.putAll(getClassPath(parent));

        for (URL url : getClassLoaderUrls(classLoader)) {
            if (url.getProtocol().equals("file")) {
                File file = toFile(url);
                if (!fileClassLoaderHashMap.containsKey(file)) {
                    fileClassLoaderHashMap.put(file, classLoader);
                }
            }
        }

        return fileClassLoaderHashMap;
    }
    private static List<URL> getClassLoaderUrls(ClassLoader classLoader){
        if(classLoader instanceof URLClassLoader)
            return List.of(((URLClassLoader) classLoader).getURLs());
        if(classLoader.equals(ClassLoader.getSystemClassLoader()))
            return getJavaClassPath();
        return List.of();
    }
    private static List<URL> getJavaClassPath(){
        List<URL> urlList=new ArrayList<>();
        for (String entry :StandardSystemProperty.JAVA_CLASS_PATH.value().split(StandardSystemProperty.PATH_SEPARATOR.value())) {
            try {
                try {
                    urlList.add(new File(entry).toURI().toURL());
                } catch (SecurityException e) { // File.toURI checks to see if the file is a directory
                    urlList.add(new URL("file", null, new File(entry).getAbsolutePath()));
                }
            } catch (MalformedURLException e) {
                LoggerMessageProccesor.getLogger().log(Level.WARNING,"Error generated url in getJavaClassPath",e);
            }
        }

        return urlList;
    }
    static File toFile(URL url) {
        if(!url.getProtocol().equals("file")){
            throw new IllegalArgumentException();
        }
        try {
            return new File(url.toURI()); // Accepts escaped characters like %20.
        } catch (URISyntaxException e) { // URL.toURI() doesn't escape chars.
            return new File(url.getPath()); // Accepts non-escaped chars like space.
        }
    }


    @Getter
    public static final class Location{
        private final File home;
        private final ClassLoader classloader;

        Location(@NonNull File home,@NonNull ClassLoader classloader) {
            this.home = home;
            this.classloader = classloader;
        }
    }
}
