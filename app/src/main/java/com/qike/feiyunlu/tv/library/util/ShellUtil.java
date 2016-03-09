package com.qike.feiyunlu.tv.library.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/** Shell工具类 */
public final class ShellUtil {
	private static ShellUtil instance = null;

	private ShellUtil() {

		// Exists only to defeat instantiation.
	}

	/** 返回ShellUtil的单例 */
	public static ShellUtil getInstance() {
		if (instance == null) {
			instance = new ShellUtil();
			instance.root();
		}
		return instance;
	}

	/** \link #root()\endlink后的进程 */
	private Process process;

	/** \link #root()\endlink后的父进程的标准输入 */
	private DataOutputStream dos;


	/**
	 * @brief 切换至ROOT用户
	 * @details 执行su命令，变更为root用户
	 * @pre 设备已经破解，否则su不可用
	 * 
	 * @return 是否成功
	 */
	public boolean root() {
		try {
			// 执行su变更用户身份为root
			process = Runtime.getRuntime().exec("su");
			// 转成DataOutputStream方便写入字符串
			dos = new DataOutputStream(process.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @brief ROOT权限下执行命令
	 * @pre 执行\link #root()\endlink
	 * 
	 * @param cmd
	 *            命令
	 */
	public boolean rootCommand(String cmd) {
		if (null != dos) {
			try {
				dos.writeBytes(cmd);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

	// /**
	// * @brief \link #rootCommand()\endlink后的结果
	// * @pre 执行\link #rootCommand()\endlink
	// *
	// * @warning 不能在stdin流输入命令后再从stdout获输出结果
	// * （之前测试版也放在不同位置试过，都不成，死锁？没找到更多资料）
	// *
	// * @return 输出结果的集合
	// */
	// public ArrayList<String> getStdout() {
	// ArrayList<String> lineArray = new ArrayList<String>();
	// try {
	// handleStdout(lineArray, process);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return lineArray;
	// }

	/** 释放占用资源 */
	public boolean rootRelease() {
		try {
			dos.writeBytes("exit\n");
			dos.flush();
			process.waitFor(); // 等待执行完成
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (null != process) {
					process.destroy();
				}
				if (null != dos) {
					dos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}



	/**
	 * 输入流转成字符串
	 * 
	 * @throws IOException
	 */
	public String inputStream2Str(InputStream is) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = is.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

}