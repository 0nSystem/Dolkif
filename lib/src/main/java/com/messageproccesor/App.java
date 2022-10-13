package com.messageproccesor;

import com.messageproccesor.annotations.Proccesor;
import com.messageproccesor.proccesor.ClassProccesor;
import com.messageproccesor.utils.StandardSystemProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Hello world!
 *
 */
@Proccesor
public class App
{

    public static void main( String[] args ) throws IOException {
        System.out.println( "Hello World!" );
        ClassProccesor.from(ClassLoader.getSystemClassLoader()).getResource().forEach(resource->{
            System.out.println(String.format("%s - %s",resource.getClassloader().getName(),resource.getHome().getPath()));
        });

    }

    public static void pruebaLectura(){
        Arrays.stream(ClassLoader.getSystemClassLoader().getDefinedPackages()).forEach(aPackage -> {

            System.out.println(aPackage.getName());
            Optional<Set<Class>> allClass=findAllClassesUsingClassLoader(aPackage.getName());
            allClass.ifPresent(classes -> classes.forEach(aClass -> {
                if (aClass != null) {
                    System.out.println(aClass);
                }
            }));
        });
    }
    public static Optional<Set<Class>> findAllClassesUsingClassLoader(String packageName) {
        try{
            String packageParsed=packageName.replaceAll("[.]", "/");

            InputStream stream = ClassLoader.getSystemClassLoader()
                    .getResourceAsStream(packageParsed);
            assert stream != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            Set<Optional<Class>> optional=reader.lines()
                    .filter(line -> line.endsWith(".class"))
                    .map(line -> getClass(line, packageName))
                    .collect(Collectors.toSet());

            return Optional.of(optional.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet()));
        }catch (NullPointerException e){

        }
        return Optional.empty();
    }

    private static Optional<Class> getClass(String className, String packageName) {
        try {
            return Optional.of(Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.'))));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return Optional.empty();
    }
}
