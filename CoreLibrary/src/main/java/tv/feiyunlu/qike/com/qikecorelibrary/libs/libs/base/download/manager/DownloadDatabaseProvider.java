package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases.DatabaseProvider;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.databases.impl.DatabaseProviderImpl;


public class DownloadDatabaseProvider implements DatabaseProvider {

	private DatabaseProvider mExternalProvider;
	private DatabaseProvider mInternalProvider;

	private String mExternalPath;
	private String mInternalPath;

	public DownloadDatabaseProvider(Context context, String databasePath) {
		mExternalPath = databasePath;
		mInternalPath = context.getDatabasePath("mzw_new_downloads.db").getAbsolutePath();
		boolean initIntenal = initInternalDao(context);
		boolean initExtenal = initExternalDao();

		if (initIntenal) {
			mInternalProvider = new DatabaseProviderImpl(mInternalPath);
		}
		if (initExtenal) {
			mExternalProvider = new DatabaseProviderImpl(mExternalPath);
		}
	}

	private boolean initInternalDao(Context context) {
		try {
			File file = new File(mInternalPath);
			boolean success = file.exists();
			if (!success) {
				File dir = file.getParentFile();
				success = dir.exists();
				if (!success) {
					success = dir.mkdirs();
				}
				if (success) {
					success = copyData(mExternalPath, mInternalPath);
				}
			}
			return success;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean initExternalDao() {
		try {
			if (!TextUtils.isEmpty(mExternalPath)) {
				File file = new File(mExternalPath);
				boolean success = file.exists();
				if (!success) {
					File dir = file.getParentFile();
					success = dir.exists();
					if (!success) {
						success = dir.mkdirs();
					}
					if (success) {
						success = copyData(mInternalPath, mExternalPath);
					}
				}
				return success;
			}
		} catch (Throwable e) {
			e.printStackTrace();

		}
		return false;
	}

	private boolean copyData(String srcPath, String destPath) {
		FileOutputStream out = null;
		FileInputStream ins = null;
		try {
			File srcFile = new File(srcPath);
			if (!srcFile.exists()) {
				return true;
			}
			out = new FileOutputStream(destPath);
			ins = new FileInputStream(srcFile);
			int len = 0;
			byte[] buff = new byte[1024];
			while ((len = ins.read(buff)) != -1) {
				out.write(buff, 0, len);
			}
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			if (ins != null) {
				try {
					ins.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public <T, ID> void save(T data) {
		try {
			if (data != null) {
				if (mInternalProvider != null) {
					mInternalProvider.save(data);
				}
				if (mExternalProvider != null) {
					mExternalProvider.save(data);
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public <T, ID> void delete(T data) {
		try {
			if (data != null) {
				if (mInternalProvider != null) {
					mInternalProvider.delete(data);
				}
				if (mExternalProvider != null) {
					mExternalProvider.delete(data);
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public <T, ID> void update(T data) {
		try {
			if (data != null) {
				if (mInternalProvider != null) {
					mInternalProvider.update(data);
				}
				if (mExternalProvider != null) {
					mExternalProvider.update(data);
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public <T, ID> List<T> queryAll(Class<T> clazz) {
		try {
			if (mInternalProvider != null) {
				return mInternalProvider.queryAll(clazz);
			}
			if (mExternalProvider != null) {
				return mExternalProvider.queryAll(clazz);
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		try {
			if (mInternalProvider != null) {
				 mInternalProvider.close();
			}
			if (mExternalProvider != null) {
				 mExternalProvider.close();
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public <T, ID> T query(Class<T> clazz, ID key) {
		try {
			if (mInternalProvider != null) {
				return mInternalProvider.query(clazz, key);
			}
			if (mExternalProvider != null) {
				return mExternalProvider.query(clazz, key);
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void init(String databasePath) {
		
	}

	@Override
	public <T, ID> List<T> queryForEq(Class<T> clazz, String arg0, Object arg1) {
		try {
			if (mInternalProvider != null) {
				return mInternalProvider.queryForEq(clazz, arg0, arg1);
			}
			if (mExternalProvider != null) {
				return mExternalProvider.queryForEq(clazz, arg0, arg1);
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

}
