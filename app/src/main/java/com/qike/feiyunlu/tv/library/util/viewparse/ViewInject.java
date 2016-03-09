package com.qike.feiyunlu.tv.library.util.viewparse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ViewInject {
	int id() default -1;
	

	String textName() default "";
	String clickMethod() default "";
	String onItemClickMethod() default "";

	int textId() default -2;

	int imgId() default -1;

	String listeners() default "";
	
	int visible() default -1;

	
}
