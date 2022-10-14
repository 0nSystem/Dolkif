package com.messageproccesor;

import com.messageproccesor.annotations.Proccesor;
import com.messageproccesor.proccesor.ClassProccesor;
import com.messageproccesor.utils.StandardSystemProperty;

import java.io.*;
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

    public static void main( String[] args ) throws IOException, ClassNotFoundException {
        System.out.println( "Hello World!" );
        ClassProccesor.from(ClassLoader.getSystemClassLoader()).getResource().forEach(resource->{
            System.out.println(String.format("%s ",resource.getResoucePath()));
        });
        System.out.println("--------");
        System.out.println();
    }

}
