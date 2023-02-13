package org.dolkif.context;

import org.junit.Assert;
import org.junit.Test;

public class ReaderClassTest {


    @Test
    public void testGetClassTypes(){
        final ReaderClass reader = ReaderClass.of(ClassLoader.getSystemClassLoader());
        Assert.assertTrue("Reader don't found class types",reader.getResource().size() > 0);
    }
}
