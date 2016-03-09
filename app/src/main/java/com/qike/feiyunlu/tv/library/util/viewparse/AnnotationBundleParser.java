package com.qike.feiyunlu.tv.library.util.viewparse;

import java.lang.reflect.Field;

public class AnnotationBundleParser implements AnnotationParser {

	@Override
	public void parse(Object obj) {
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(BundleInject.class)) {
					BundleInject inject = field
							.getAnnotation(BundleInject.class);
					String name = inject.name();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

    @Override
    public void parse(Object obj,String simpleName){
        // TODO Auto-generated method stub
        
    }

}
