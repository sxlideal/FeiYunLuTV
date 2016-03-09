package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.exception;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 
 * <p>
 * TODO(异常处理类)
 * </p>
 * <br/>
 * <p>
 * TODO (对于异常的处理以及崩溃性异常的捕获，异常本地保存)
 * </p>
 * 
 * @since 1.0.0
 * @author wangweiqiang
 */
public class ExceptionManager implements UncaughtExceptionHandler {
	private UncaughtExceptionHandler mHandler;
	private Delivery mDelivery;
	private static ExceptionManager mException;

	private ExceptionManager() {
		mHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	public static synchronized ExceptionManager getInstance() {
		if (mException == null) {
			mException = new ExceptionManager();
		}
		return mException;
	}

	/**
	 * 
	 *<p>TODO（异常处理方法)</p><br/>
	 *<p>TODO(对于异常的处理方法)</p>
	 * @since 1.0.0
	 * @author wangweiqiang
	 * @param ex
	 */
	public void processException(Throwable ex) {
		if (ex == null) {
			return;
		}
		catchException(Level.ERROR, ex);

	}

	/**
	 * 
	 *<p>TODO(注册异常监听器)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author wangweiqiang
	 * @param level
	 * @param delivery 异常处理监听器
	 */
	public void registerDelivery(Delivery delivery) {

		mDelivery = delivery;
	}

	/**
	 * 
	 *<p>TODO(移除异常监听器)</p><br/>
	 *<p>TODO(详细描述)</p>
	 * @since 1.0.0
	 * @author wangweiqiang
	 * @param level
	 * @param delivery 异常处理监听器
	 */
	public void unregisterDelivery(Delivery delivery) {
		mDelivery = null;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {

		if (throwable != null) {
			boolean flag = catchException(Level.CRASH, throwable);
			if (!flag && mHandler != null) {
				mHandler.uncaughtException(thread, throwable);
			}
		}

	}

	private boolean catchException(Level level, Throwable throwable) {
		if (mDelivery != null) {
			return mDelivery.onCatch(level, throwable);
		}
		return false;
	}

}
