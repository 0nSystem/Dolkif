package org.dolkif.context;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReaderClassTest {


    @Test
    public void testGetClassTypes(){
        final ReaderClass reader = ReaderClass.of(ClassLoader.getSystemClassLoader());
        Assertions.assertTrue(reader.getResource().size() > 0);
    }
}
