package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager;

import java.lang.reflect.Method;

public class ListenerCommand implements Runnable {

	private Object listener;
	private Object[] args;
	private String methodName;
	
	
	

	public ListenerCommand(Object listener, String methodName,Object... args) {
		this.listener = listener;
		this.args = args;
		this.methodName = methodName;
	}




	@SuppressWarnings("rawtypes")
	@Override
	public void run() {
		try {
			Class clazz = listener.getClass();
			Method method = null;
			if (args == null || args.length == 0) {
				method = clazz.getMethod(methodName);
				method.invoke(listener);
			} else {
				Class[] argClasses = new Class[args.length];

				for (int i = 0; i < args.length; i++) {
					argClasses[i] = args[i].getClass();
				}
				method = clazz.getMethod(methodName, argClasses);
				method.invoke(listener, args);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
