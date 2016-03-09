package com.qike.feiyunlu.tv.library.util.viewparse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BundleInject {
	String name();
}
