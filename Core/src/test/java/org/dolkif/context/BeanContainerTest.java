package org.dolkif.context;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

public class BeanContainerTest {

    IBeansContainer beansContainer = new BeansContainer();

    @BeforeEach
    public void configurationBeanContainer(){
        beansContainer.addBean(
                new Bean.Type<>(new Bean.Configuration(null, Bean.ScopePattern.SINGLETON),
                        String.class)
        );
        beansContainer.addBean(
                new Bean.Instance<>(new Bean.Configuration("string1", Bean.ScopePattern.SINGLETON),
                        String.class)
        );
        beansContainer.addBean(
                new Bean.Instance<>(new Bean.Configuration(null, Bean.ScopePattern.SINGLETON),
                        Integer.class)
        );
        beansContainer.addBean(
                new Bean.Type<>(new Bean.Configuration("integer1", Bean.ScopePattern.SINGLETON),
                        Integer.class)
        );
    }
    @Test
    public void testAddBeanContainer(){
        val beanToCopyTwoTimeFirstAddAndSecondCantAdd = new Bean.Instance<>(
                new Bean.Configuration(null, Bean.ScopePattern.PROTOTYPE),
                Integer.class
        );

        Assertions.assertAll(() -> {
            Assertions.assertTrue(
                    beansContainer.addBean(
                            new Bean.Type<>(new Bean.Configuration(null, Bean.ScopePattern.PROTOTYPE),
                                    String.class)
                    )
            );
            Assertions.assertTrue(beansContainer.addBean(beanToCopyTwoTimeFirstAddAndSecondCantAdd));
            Assertions.assertFalse(beansContainer.addBean(beanToCopyTwoTimeFirstAddAndSecondCantAdd));
            Assertions.assertEquals(6,beansContainer.getAllBeans().size());

        });
    }

    @Test
    public void testFilterBeanContainer(){
        //TODO REVISION
        val beanReferenceToFindNotNotQualify = new Bean.BeanReference<>(Bean.TypeReference.NULL,String.class,new Annotation[]{});
        val a = beansContainer.filterBean(beanReferenceToFindNotNotQualify);
        Assertions.assertAll(() -> {
            Assertions.assertEquals(1,beansContainer.filterBean(beanReferenceToFindNotNotQualify).size());
            Assertions.assertTrue(beansContainer.findBean(beanReferenceToFindNotNotQualify).isPresent());
        });

    }
}
