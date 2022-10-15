package com.messageproccesor;

import com.messageproccesor.annotations.Proccesor;
import com.messageproccesor.model.HandlerProcessor;
import com.messageproccesor.proccesor.ClassProccesor;
import com.messageproccesor.proccesor.MessageProccesorRunner;
import com.messageproccesor.utils.StandardSystemProperty;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URLClassLoader;
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
        MessageProccesorRunner.run(App.class);
        System.out.println("------------");
        MessageProccesorRunner.getHandlerProcessorGroupingrepositories().entrySet().forEach(classSetEntry -> {
            System.out.println("key->"+classSetEntry.getKey().getTypeName()+", value->"+classSetEntry.getValue().toString());
        });

    }

}
