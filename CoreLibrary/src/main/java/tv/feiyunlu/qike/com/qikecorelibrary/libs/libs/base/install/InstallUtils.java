package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl.SecurityUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.security.Base;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.FileUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.utils.EnvUtils;


public class InstallUtils {

	public static final String MAINIFEST_DAT = "mainifest.dat";
	public static final String ICON = "icon.png";
	public static final String APPLICATION_APK = "application.apk";
	public static final String DATA_DAT = "data.dat";

	public static long getCrcValue(String filePath, String entryName) {
		try {
			ZipFile zipFile = new ZipFile(filePath);
			ZipEntry entry = zipFile.getEntry(entryName);
			return entry.getCrc();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public static void zipToDirectory(String sZipPathFile, String sDestPath,
			long totalSize, InstallTask task) throws Exception {
		FileInputStream fins = null;
		long previousTime = 0;
		ZipFile zf = null;
		try {
			zf = new ZipFile(sZipPathFile);
			Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zf
					.entries();
			byte ch[] = new byte[2048];
			long progress = 0;

			while (entries.hasMoreElements()) {
				if (task.isStoped()) {
					break;
				}
				ZipEntry ze = entries.nextElement();
				if (ze == null) {
					break;
				}
				String catalogue = ze.getName().replace("\\", "/");
				File zfile = new File(sDestPath + "//" + catalogue);

				if (catalogue.equals("application.apk")
						|| catalogue.equals("icon.png")
						|| catalogue.equals("mainifest.dat")) {
					continue;
				}
				File fpath = new File(zfile.getParentFile().getPath());

				if (ze.isDirectory()) {
					if (!zfile.exists()) {
						zfile.mkdirs();
					}

				} else {
					if (!fpath.exists()) {
						fpath.mkdirs();
					}

					if (zfile.getParentFile() != null
							&& !zfile.getParentFile().exists()) {
						zfile.getParentFile().mkdirs();
					}
					FileOutputStream fouts = null;
					int i;
					InputStream ins = null;
					try {
						fouts = new FileOutputStream(zfile, false);
						ins = zf.getInputStream(ze);
						while ((i = ins.read(ch)) != -1) {
							if (task.isStoped()) {
								break;
							}
							fouts.write(ch, 0, i);
							progress += i;
							long time = System.currentTimeMillis()
									- previousTime;
							if (time > 1500) {

								task.pulishProgress(progress, totalSize);
								previousTime = System.currentTimeMillis();
							}

						}
					} catch (Exception e) {
						throw e;
					} finally {
						if (ins != null) {
							try {
								ins.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (fouts != null) {
							try {
								fouts.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (fins != null) {
					fins.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static int checkGpk(InstallManifest mainifest, File gpkFile)
			throws Throwable {

		long apkCRC32_ = getCrcValue(gpkFile.getAbsolutePath(), APPLICATION_APK);
		if (mainifest.getApkCRC32() != apkCRC32_) {
			return ResultCode.APKCRC32_INCORRECT;
		}

		if (mainifest.getSdkVersion() > Integer
				.valueOf(android.os.Build.VERSION.SDK)) {
			return ResultCode.SDKVERSION_INCORRECT;
		}

		String cpuType_ = EnvUtils.getCpuType();

		int allCpuType;
		if (mainifest.getGpkVersion().equals("old")) {
			allCpuType = 15;
		} else {
			allCpuType = -1;
		}

		String[] typeArr = new String[5];
		String cpuType = mainifest.getCpuType();
		int ct = Integer.valueOf(cpuType);
		typeArr[0] = ((ct & 1) == 1) ? "Tegra" : "";
		typeArr[1] = ((ct & 2) == 2) ? "S" : "";
		typeArr[2] = ((ct & 4) == 4) ? "Omap" : "";
		typeArr[3] = ((ct & 8) == 8) ? "Msm&qsd" : "";
		typeArr[4] = ((ct & 16) == 16) ? "s5pc210" : "";
		if (cpuType_.contains("exynos")) {
			cpuType_ = "s5pc210";
		}
		if (cpuType_.contains("MT") || cpuType_.contains("mt")) {
			if (((ct & 4) == 4)) {
				cpuType_ = "omap3";
			}
			if (((ct & 2) == 2)) {
				cpuType_ = "s5pc110";
			}
		}
		String cpuTypeLowerCase = TextUtils.isEmpty(cpuType_) ? "" : cpuType_
				.toLowerCase();
		outter: if (ct != allCpuType) {
			boolean flag = false;
			for (int i = 0; i < typeArr.length; i++) {
				if ("".equals(typeArr[i])) {
					continue;
				}
				if (typeArr[i].contains("&")) {
					String[] subStr = typeArr[i].split("&");
					for (int j = 0; j < subStr.length; j++) {
						flag = cpuTypeLowerCase.startsWith(subStr[j]
								.toLowerCase());
						if (flag) {
							break outter;
						}
					}
				}
				flag = cpuTypeLowerCase.startsWith(typeArr[i].toLowerCase());
				if (flag) {
					break outter;
				}
			}
			return ResultCode.CPUTYPE_INCORRECT;
		}
		return ResultCode.INSTALL_SUCCESS;
	}

	public static InstallManifest getManifest(String gpkPath, String tempPath)
			throws InstallParseException {

		try {
			File gpkFile = new File(gpkPath);
			if (!gpkFile.exists()) {
				return null;
			}
			File dir = new File(tempPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			zipToFile(gpkFile.getAbsolutePath(), dir.getAbsolutePath(),
					MAINIFEST_DAT, null);
			byte[] data = FileUtils.read(dir.getAbsolutePath() + "/"
					+ MAINIFEST_DAT);

			String manifestStr = new String(data, "UTF-8");
			data = Base.decode(manifestStr);
			data = SecurityUtils.decrypt(data);
			manifestStr = new String(data, "gbk");

			InstallManifest installManifest = jsonTransMainifest(manifestStr);

			return installManifest;
		} catch (Exception e) {
			throw new InstallParseException(e);
		}

	}

	public static long calcuteDataSize(String sZipPathFile) {
		long length = 0;
		try {
			ZipFile zf = new ZipFile(sZipPathFile);
			Enumeration<? extends ZipEntry> entries = zf.entries();
			ZipEntry ze = null;
			while (entries.hasMoreElements()) {
				if ((ze = entries.nextElement()) != null) {
					String catalogue = ze.getName().replace("\\", "/");
					if (catalogue.equals("application.apk")
							|| catalogue.equals("icon.png")
							|| catalogue.equals("mainifest.dat")) {
						continue;
					}
					if (ze.isDirectory()) {
						continue;
					}
					length += ze.getSize();
				}

			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return length;
	}

	public static int installSilent(String path, String packageName,
			boolean applyInstallSdcard, boolean installSdcard) {
		PrintStream ps = null;
		try {
			final Process p = Runtime.getRuntime().exec("su");
			ps = new PrintStream(p.getOutputStream());
			ps.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
			ps.flush();
			String installStr = "pm install -r " + path;
			if (installSdcard && applyInstallSdcard) {
				installStr = "pm install -r -s " + path;
			}

			ps.println(installStr);
			ps.println("exit");
			ps.flush();
			p.waitFor();
			int i = p.exitValue();
			if (i == 0) {
				return ResultCode.INSTALL_SUCCESS;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		if (installSdcard) {
			return installSilent(path, packageName, false, false);
		}
		return ResultCode.ROOT_INSTALL_FAIL_DEFAULT;
	}

	public static void defaultInstall(File apkFile, Context context) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(apkFile),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static InstallManifest jsonTransMainifest(String jsonStr)
			throws JSONException {
		InstallManifest mainifest = new InstallManifest();
		JSONObject rootJson = new JSONObject(jsonStr);

		JSONObject phoneBaseInfo = rootJson.getJSONObject("gpkBaseInfo");
		String cpuType = phoneBaseInfo.getString("cpuType");
		mainifest.setCpuType(cpuType); // 1

		int sdkVersion = phoneBaseInfo.getInt("sdkVersion");
		mainifest.setSdkVersion(sdkVersion);

		String screenDensity = phoneBaseInfo.getString("screenDensity");
		mainifest.setScreenDensity(screenDensity); // 2

		JSONObject dataValidation = rootJson.getJSONObject("dataValidation");
		long apkCRC32 = dataValidation.getLong("apkCRC32");
		// long dataCRC32 = dataValidation.getLong("dataCRC32");

		mainifest.setApkCRC32(apkCRC32); // 3
		// mainifest.setDataCRC32(dataCRC32); // 4

		JSONObject dataBaseInfo = rootJson.getJSONObject("dataBaseInfo");
		String copyPath = dataBaseInfo.getString("copyPath");
		copyPath = copyPath.replace("\n", "");
		mainifest.setCopyPath(copyPath); // 5
		//		System.out.println("copyPath : " + copyPath);
		// long dataSize = dataBaseInfo.getLong("dataSize");
		// mainifest.setDataSize(dataSize); // 10

		JSONObject baseInfoJson = rootJson.getJSONObject("apkBaseInfo");
		String appName = baseInfoJson.getString("appName");
		mainifest.setAppName(appName); // 6
		int appSize = baseInfoJson.getInt("appSize");
		mainifest.setAppSize(appSize); // 7
		String packageName = baseInfoJson.getString("packageName");
		mainifest.setPackageName(packageName); // 8
		String versionName = baseInfoJson.getString("versionName");
		mainifest.setVersionName(versionName); // 9

		String gpkVersion = rootJson.optString("gpkVersion");
		if (TextUtils.isEmpty(gpkVersion)) {
			mainifest.setGpkVersion("old");
		} else {
			mainifest.setGpkVersion("new");
		}
		// try {
		// Integer.parseInt(gpkVersion);
		// mainifest.setGpkVersion("old");
		// } catch (Exception e) {
		// mainifest.setGpkVersion("new");
		// }
		return mainifest;
	}

	public static void zipToFile(String zipPath, String destPath,
			String fileName, InstallTask task) throws Exception {
		InputStream ins = null;
		FileOutputStream out = null;
		ZipFile zf = null;

		try {
			zf = new ZipFile(zipPath);
			ZipEntry ze = zf.getEntry(fileName);
			ins = zf.getInputStream(ze);
			byte[] buff = new byte[2048];
			int len = 0;
			File zfile = new File(destPath + "//" + ze.getName());
			File fpath = new File(zfile.getParentFile().getAbsolutePath());
			if (!fpath.exists()) {
				fpath.mkdirs();
			}
			out = new FileOutputStream(zfile);
			while ((len = ins.read(buff)) != -1) {
				out.write(buff, 0, len);
				if (task != null && task.isStoped()) {
					break;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			try {
				if (ins != null) {
					ins.close();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			try {
				if (zf != null) {
					zf.close();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}
}
