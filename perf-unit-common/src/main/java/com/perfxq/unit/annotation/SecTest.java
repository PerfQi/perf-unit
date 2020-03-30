package com.perfxq.unit.annotation;


import com.perfxq.unit.datasource.dynamic.SecTestConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@EnableAutoConfiguration
@Import({SecTestConfiguration.class})
public @interface SecTest {

    boolean enablePrepare() default false;

    PrepareDataType prepareDateType() default PrepareDataType.CSV2DB;

    String[] prepareDateConfig() default {};

    boolean enableCheck() default false;

    String[] checkConfigFiles() default {};
}
