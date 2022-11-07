package com.messageproccesor.proccesor.Loaders;


import com.messageproccesor.proccesor.BeansContainer;
import org.junit.Assert;
import org.junit.Test;

public class TestLoaderInstance {

    @Test
    public void instanceObjectWithEmptyConstructor(){

        BeansContainer beansContainer = new BeansContainer();
        LoaderInstances loaderInstances = new LoaderInstances(beansContainer);
        String text = null;
        try {
            text = loaderInstances.loadInstancesWithPatternExecute(String.class);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertNotEquals(null,text);
    }

    @Test
    public void instaceObjectWithParamsInConstructor(){

        /*
        OutputStream outputStream = OutputStream.nullOutputStream();

        BeansContainer beansContainer = new BeansContainer();
        beansContainer.insertObjectSingleton(beansContainer);
        LoaderInstances loaderInstances = new LoaderInstances(beansContainer);

        PrintWriter printWriter = null;
        try {
            printWriter = loaderInstances.loadInstancesWithPatternExecute(PrintWriter.class);
        } catch (Exception e) {

        }
        Assert.assertNotNull(printWriter);

         */

    }
}
