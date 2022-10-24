package com.messageproccesor;
import com.messageproccesor.proccesor.MessageProccesorRunner;
import com.messageproccesor.proccesor.ProcessExecutor;



/**
 * Hello world!
 *
 */
public class App
{

    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        MessageProccesorRunner.run(App.class);
        System.out.println("------------");
        MessageProccesorRunner.getHandlerProcessorGroupingrepositories().entrySet().forEach(classSetEntry -> {
            System.out.println("key->"+classSetEntry.getKey().getTypeName()+", value->"+classSetEntry.getValue().toString());
        });

        ProcessExecutor processExecutor=new ProcessExecutor();
        ObjectProccesedTest objetToProcessed=new ObjectProccesedTest();

        processExecutor.exec(objetToProcessed);


    }

}
