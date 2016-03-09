package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.logger;

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.config.Configuration;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.exception.ExceptionManager;


/**
 * 
 * <p>
 * (log日志输出的核心类)
 * </p>
 * <br/>
 * <p>
 * (根据用户配置MzwLoggerConfig的状态以及传入的tag类型的不同设置log日志输入状态，存储状态以及输出标签)
 * </p>
 * 
 * @since 1.0.0
 * @author wangweiqiang
 */
public class Logger implements ILogger {

	private static Method LOGI = null;
	private static Method LOGE = null;
	private static Method LOGD = null;
	private static Method LOGV = null;
	private static Method LOGW = null;

	static {
		try {
			LOGI = Log.class.getMethod("i", String.class, String.class);
			LOGE = Log.class.getMethod("e", String.class, String.class);
			LOGD = Log.class.getMethod("d", String.class, String.class);
			LOGV = Log.class.getMethod("v", String.class, String.class);
			LOGW = Log.class.getMethod("w", String.class, String.class);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static HashMap<Object, Logger> LOGGERS = new HashMap<Object, Logger>();

	public static Logger getLogger(Object tag) {

		Logger logger = LOGGERS.get(tag);
		if (logger == null) {
			logger = new Logger(tag);
		}

		return logger;

	}

	private String mTag;
	private boolean mDebug = true;
	private boolean mOpen = true;
	private String mStorepath = "";
	private boolean mStore = false;

	/**
	 * 
	 * <p>
	 * (设置log日志输出的标签)
	 * </p>
	 * <br/>
	 * <p>
	 * (根据传入的参数类型以及MzwLoggerConfig的配置来确定输出的tag标签)
	 * </p>
	 * 
	 * @since 1.0.0
	 * @author wangweiqiang
	 * @param tag
	 *            ：判断tag传入的类型是否为基本数据类型，如果是的话转化为String作为tag.
	 *            如果不是根据MzwLoggerConfig配置的tag参数设置tag标签
	 *            ，如果没有配置MzwLoggerConfig，tag为当前类的类名
	 * @throws MzwNoTagException
	 */
	public Logger(Object tag) {
		if (tag == null) {
			throw new RuntimeException("tag不能为空，请传入相应参数");
		}
		if (isBaseType(tag)) {
			mTag = String.valueOf(tag);
		} else {

			boolean isAnnotation = tag.getClass().isAnnotationPresent(
					LoggerConfig.class);
			if (isAnnotation) {
				LoggerConfig config = tag.getClass().getAnnotation(
						LoggerConfig.class);

				if (TextUtils.isEmpty(config.tag())) {
					mTag = tag.getClass().getSimpleName();
				} else {
					mTag = config.tag();
				}

				mDebug = config.debug();
				mOpen = config.open();
				mStore = config.store();
				mStorepath = config.storePath();
			} else {
				mTag = tag.getClass().getSimpleName();

			}

		}
	}

	@Override
	public void i(Object msg) {
		log(LoggerLevel.I, msg);

	}

	@Override
	public void d(Object msg) {
		log(LoggerLevel.D, msg);
	}

	@Override
	public void e(Object msg) {
		log(LoggerLevel.E, msg);
	}

	@Override
	public void v(Object msg) {

		log(LoggerLevel.V, msg);
	}

	@Override
	public void w(Object msg) {
		log(LoggerLevel.W, msg);
	}

	private void log(LoggerLevel level, Object obj) {
		Method method = null;
		String msg = String.valueOf(obj);
		switch (level) {
		case D:
			method = LOGD;
			break;
		case E:
			method = LOGE;
			break;
		case I:
			method = LOGI;
			break;
		case V:
			method = LOGV;
			break;
		case W:
			method = LOGW;
			break;

		default:
			break;
		}
		try {
			if (mOpen && method != null) {
				if (Configuration.getConfiguration().isDebug()) {
					method.invoke(null, mTag, msg);
				} else {
					if (!mDebug) {
						method.invoke(null, mTag, msg);
					}
				}
			}
			if (mStore) {
				saveLoggerToFile(msg);
			}
		} catch (Throwable e) {
			ExceptionManager.getInstance().processException(e);
		}
	}

	/**
	 * 
	 * <p>
	 * (将log日志保存到本地)
	 * </p>
	 * <br/>
	 * <p>
	 * (根据MzwLoggerConfig配置的存储状态是否对log日志进行本地存储，以及本地存储的位置)
	 * </p>
	 * 
	 * @since 1.0.0
	 * @author wangweiqiang
	 */
	private void saveLoggerToFile(String logMessage) {

		if (TextUtils.isEmpty(mStorepath)) {
			throw new RuntimeException("storepath没有配置，请配置storePath");
		}
		// SerializeManagerV1Impl.getInstance().save(LoggerStore.class,
		// mStorepath, logMessage);
	}

	/**
	 * 
	 * <p>
	 * (判断传入的tag类型)
	 * </p>
	 * <br/>
	 * <p>
	 * (判断传入的tag类型，如果为基本数据类型返回true,否则返回false)
	 * </p>
	 * 
	 * @since 1.0.0
	 * @author wangweiqiang
	 * @param tag
	 *            传入的tag标签
	 * @return 如果为基本数据类型返回true,否则返回false
	 */
	private boolean isBaseType(Object tag) {

		if (tag.getClass() != String.class && tag.getClass() != Integer.class
				&& tag.getClass() != Boolean.class
				&& tag.getClass() != Float.class
				&& tag.getClass() != Double.class
				&& tag.getClass() != Long.class
				&& tag.getClass() != Character.class
				&& tag.getClass() != char.class && tag.getClass() != int.class
				&& tag.getClass() != boolean.class
				&& tag.getClass() != float.class
				&& tag.getClass() != long.class && tag.getClass() != int.class
				&& tag.getClass() != double.class) {
			return false;
		}
		return true;
	}

	private static enum LoggerLevel {
		I, D, E, V, W
	}
}
