package com.popeye.orm.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * USER_MANUAL: This annotation is used to define the name of the table in the database
 */

@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";

    boolean readOnly() default false;

    boolean isView() default false;
}
