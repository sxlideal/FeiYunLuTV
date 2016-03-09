package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by zhulingjun on 14-3-25.
 */
public class CmdUtils {

    private static int SUCCESS = 0;
    private static int ERROR = -1;

    
    
    
    /**
	 * 判断是否可能有ROOT权限
	 * @return
	 */
	public static boolean haveRoot() {
		try {
			File file = new File("/system/bin/su");
			File file2 = new File("/system/xbin/su");
			return file.exists() || file2.exists();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;

	}
    /**
     * 需要ROOT权限
     * @param cmds
     * @return
     */
    public static int executeSu(List<String> cmds){
        if(cmds.isEmpty()){
            return SUCCESS;
        }
        PrintStream ps = null;
        try {
            final Process p = Runtime.getRuntime().exec("su");
            ps = new PrintStream(p.getOutputStream());

            new Thread(){
                @Override
                public void run() {
                    try {
                        final BufferedReader reader  = new  BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line = null;
                        while((line = reader.readLine()) != null){
                            Log.i("mzw_debug_success", line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            new Thread(){
                @Override
                public void run() {
                    try {
                        final BufferedReader errorReader = new  BufferedReader(new InputStreamReader(p.getErrorStream()));
                        String line = null;
                        while((line = errorReader.readLine()) != null){
                            Log.i("mzw_debug_error", line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            if(cmds != null){
                for (String execCmd : cmds) {
                    ps.println(execCmd);
                    ps.flush();
                }
            }
            ps.println("exit");
            ps.flush();

            p.getOutputStream().close();
            p.waitFor();
            int code = p.exitValue();
            return code;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ERROR;
    }

    /**
     * 需要ROOT权限
     * @param cmd
     * @return
     */
    public static int executeSu(String cmd){
        if(TextUtils.isEmpty(cmd)){
            return SUCCESS;
        }

        PrintStream ps = null;
        try {
            final Process p = Runtime.getRuntime().exec("su");
            ps = new PrintStream(p.getOutputStream());

            new Thread(){
                @Override
                public void run() {
                    try {
                        final BufferedReader reader  = new  BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line = null;
                        while((line = reader.readLine()) != null){
                            Log.i("mzw_debug_success", line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            new Thread(){
                @Override
                public void run() {
                    try {
                        final BufferedReader errorReader = new  BufferedReader(new InputStreamReader(p.getErrorStream()));
                        String line = null;
                        while((line = errorReader.readLine()) != null){
                            Log.i("mzw_debug_error", line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();


            ps.println(cmd);
            ps.flush();
            ps.println("exit");
            ps.flush();

            p.getOutputStream().close();
            p.waitFor();
            int code = p.exitValue();
            return code;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ERROR;
    }


    /**
     * 无需ROOT权限
     * @param cmds
     * @return
     */
    public static int executeShell(List<String> cmds){
        if(cmds.isEmpty()){
            return SUCCESS;
        }

        PrintStream ps = null;
        try {
            final Process p = Runtime.getRuntime().exec("sh");
            ps = new PrintStream(p.getOutputStream());

            new Thread(){
                @Override
                public void run() {
                    try {
                        final	BufferedReader reader  = new  BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line = null;
                        while((line = reader.readLine()) != null){
                            Log.i("mzw_debug_success", line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            new Thread(){
                @Override
                public void run() {
                    try {
                        final BufferedReader errorReader = new  BufferedReader(new InputStreamReader(p.getErrorStream()));
                        String line = null;
                        while((line = errorReader.readLine()) != null){
                            Log.i("mzw_debug_error", line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            if(cmds != null){
                for (String execCmd : cmds) {
                    ps.println(execCmd);
                    ps.flush();
                }
            }
            ps.println("exit");
            ps.flush();

//			p.getOutputStream().close();
            p.waitFor();
            int code = p.exitValue();
            return code;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ERROR;
    }

    /**
     * 无需ROOT权限
     * @param cmd
     * @return
     */
    public static int executeShell(String cmd){
        if(TextUtils.isEmpty(cmd)){
            return SUCCESS;
        }

        PrintStream ps = null;
        try {
            final Process p = Runtime.getRuntime().exec("sh");
            ps = new PrintStream(p.getOutputStream());

            new Thread(){
                @Override
                public void run() {
                    try {
                        final	BufferedReader reader  = new  BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line = null;
                        while((line = reader.readLine()) != null){
                            Log.i("mzw_debug_success", line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            new Thread(){
                @Override
                public void run() {
                    try {
                        final BufferedReader errorReader = new  BufferedReader(new InputStreamReader(p.getErrorStream()));
                        String line = null;
                        while((line = errorReader.readLine()) != null){
                            Log.i("mzw_debug_error", line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();


            ps.println(cmd);
            ps.flush();

            ps.println("exit");
            ps.flush();

//			p.getOutputStream().close();
            p.waitFor();
            int code = p.exitValue();
            return code;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ERROR;
    }
}
