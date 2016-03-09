package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.apken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.ZipFile;

public class ApkEN {

	public static void decode(String srcPath) throws Exception{
		try {
			ZipAnalyze za = new ZipAnalyze(new File(srcPath));
			za.Analyze();
			byte[] m1 = { 8 };
			za.alterzip(m1);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static boolean needDecode(String srcPath){
		ZipFile zf = null;
		boolean flag = false;
		try {
			zf = new ZipFile(srcPath);
			 zf.getEntry("classes.dex");
		} catch (Throwable e) {
			e.printStackTrace();
			flag = true;
		}finally{
			if (zf != null) {
				try {
					zf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	public static void main(String[] args) {
		if (args[0].equals("e")) {
			System.out.println("开始加密");
			ZipAnalyze za = new ZipAnalyze(new File(args[1]));
			za.Analyze();

			byte[] m1 = { 9, 8 };
			za.alterzip(m1);
			System.out.println("加密完成");
		} else if (args[0].equals("d")) {
			System.out.println("开始解密");
			ZipAnalyze za = new ZipAnalyze(new File(args[1]));
			za.Analyze();

			byte[] m1 = { 8 };
			za.alterzip(m1);
			System.out.println("解密完成");
		} else {
			System.out.println("Please use the command format:");
			System.out.println("\tEncryption: java -jar APKEN.jar e xxx.apk");
			System.out.println("\tDecryption: java -jar APKEN.jar d xxx.apk");
		}
	}

	static void ApkDecryption(String file) {
		System.out.println("APK解密开始！");
		byte[] f_key1 = { 80, 75, 3, 4, 20, 0, 8, 0, 8 };
		byte[] r_key1 = { 80, 75, 3, 4, 20, 0, 9, 8, 8 };
		long skip = SearchBytes(r_key1, f_key1, new File(file), 0L, 1);

		System.out.println("第一步解密完成！");
		byte[] f_key2 = { 80, 75, 1, 2, 20, 0, 20, 0, 8, 0, 8 };
		byte[] r_key2 = { 80, 75, 1, 2, 20, 0, 20, 0, 9, 8, 8 };
		SearchBytes(r_key2, f_key2, new File(file), skip, 2);

		System.out.println("第二步解密完成！");
		System.out.println("APK解密完成！");
	}

	static void ApkEncryption(String file) {
		System.out.println("APK加密开始！");
		byte[] f_key1 = { 80, 75, 3, 4, 20, 0, 8, 0, 8 };
		byte[] r_key1 = { 80, 75, 3, 4, 20, 0, 9, 8, 8 };
		long skip = SearchBytes(f_key1, r_key1, new File(file), 0L, 1);

		System.out.println("第一步加密完成！跳转地址：" + skip);
		byte[] f_key2 = { 80, 75, 1, 2, 20, 0, 20, 0, 8, 0, 8 };
		byte[] r_key2 = { 80, 75, 1, 2, 20, 0, 20, 0, 9, 8, 8 };
		SearchBytes(f_key2, r_key2, new File(file), skip, 2);

		System.out.println("第二步加密完成！");
		System.out.println("APK加密完成！");
	}

	static long SearchBytes(byte[] f_key, byte[] r_key, File file, long jump,
			int step) {
		int sum = 0;
		long skip = 0L;
		try {
			RandomAccessFile rw = new RandomAccessFile(file, "rw");
			long Point = 0L;
			long FPoint1 = 0L;
			long FPoint = 0L;
			byte[] Pointer = new byte[1];
			int byteid = 0;
			rw.seek(jump);
			while (rw.read(Pointer) > 0) {
				if (byteid > 4) {
					System.out.println("byteid：" + byteid);
				}

				if (byteid < f_key.length) {
					if (byteid == 6) {
						System.out.println("byteid：" + Pointer[0]);
					}

					if (Pointer[0] == f_key[byteid]) {
						if ((byteid == 3) && (Pointer[0] == 4)) {
							FPoint1 = rw.getFilePointer();
							FPoint = rw.getFilePointer() + 14L;
							System.out.println("读取文件长度：" + FPoint);
							rw.seek(FPoint);
							byte[] filesize = new byte[4];
							rw.read(filesize);
							long Lfilesize = FormatConversion
									.bytes2intD(filesize);
							FPoint = rw.getFilePointer() + 8L + Lfilesize;

							rw.seek(FPoint1);
							FPoint1 = 0L;

							skip = rw.getFilePointer();
						}

						if (byteid == 0) {
							Point = rw.getFilePointer() - 1L;
						}
						byteid++;
					} else {
						byteid = 0;
					}

				} else {
					System.out.println("匹配地址：" + Point);

					rw.seek(Point);
					rw.write(r_key);
					if (FPoint != 0L) {
						rw.seek(FPoint);
					}

					byteid = 0;
					sum++;
				}
				if ((FPoint != 0L) && (step != 2) && (byteid == 7)
						&& (Pointer[0] != 8)) {
					System.out.println("byteid：" + byteid + "Pointer[0]"
							+ Pointer[0]);
					System.out.println("跳转地址：" + FPoint);

					rw.seek(FPoint);
					FPoint = 0L;
				}

			}

			rw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return skip;
	}
}
