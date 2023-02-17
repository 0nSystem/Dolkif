package org.dolkif.context;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.List;

public class BeanContainerTest {

    IBeansContainer beansContainer = new BeansContainer();

    @BeforeEach
    public void configurationBeanContainer(){
        beansContainer.addBean(
                Bean.BeanBase.of(new Bean.Configuration(null, Bean.ScopePattern.SINGLETON),
                        String.class)
        );
        beansContainer.addBean(
                Bean.BeanBase.of(new Bean.Configuration("string1", Bean.ScopePattern.SINGLETON),
                        String.class)
        );
        beansContainer.addBean(
                Bean.BeanBase.of(new Bean.Configuration(null, Bean.ScopePattern.SINGLETON),
                        2)
        );
        beansContainer.addBean(
                Bean.BeanBase.of(new Bean.Configuration("integer1", Bean.ScopePattern.SINGLETON),
                        Integer.class)
        );
    }
    @Test
    public void testAddBeanContainer(){
        val beanToCopyTwoTimeFirstAddAndSecondCantAdd = new Bean.Instance<>(
                new Bean.Configuration(null, Bean.ScopePattern.PROTOTYPE),
                Double.class
        );

        Assertions.assertAll(() -> {
            Assertions.assertTrue(beansContainer.addBean(beanToCopyTwoTimeFirstAddAndSecondCantAdd));
            Assertions.assertFalse(beansContainer.addBean(beanToCopyTwoTimeFirstAddAndSecondCantAdd));
            Assertions.assertEquals(5,beansContainer.getAllBeans().size());

        });
    }

    @Test
    public void testFilterBeanContainer(){
        //If not contains qualify get all
        val beanReferenceToFindNotNotQualify = new Bean.BeanReference<>(Bean.TypeReference.NULL,String.class,new Annotation[]{});
        val a = beansContainer.filterBean(beanReferenceToFindNotNotQualify);
        Assertions.assertAll(() -> {
            Assertions.assertEquals(2,beansContainer.filterBean(beanReferenceToFindNotNotQualify).size());
            Assertions.assertTrue(beansContainer.findBean(beanReferenceToFindNotNotQualify).isPresent());
        });

    }

    @Test
    public void testFilterBeansByClassType(){
        List<Bean.BeanBase<?>> beanBases = beansContainer.filterBean(Integer.class);
        Assertions.assertEquals(2,beanBases.size());
    }


}
