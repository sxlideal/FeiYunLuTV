package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.impl;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.CmdUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.Mount;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.MountUtils;


/**
 * Created by zhulingjun on 14-3-24.
 */
public class ShellMount implements Mount {
    private Context mContext = null;

    /**
     *
     * 写命令到/data/data/<packagename>/debugger
     * 写命令到/data/data/<packagename>/run.sh
     * 通过sh命令运行run.sh
     *
     * run.sh内容：
     *   `转移debugger文件替换/system/bin/debuggerd
     *   `start debuggerd
     */
    public ShellMount(Context context){
        mContext = context;
    }
    private ShellMount() {}

    @Override
    public int mount(String path, String targetpath) {
        work(MountUtils.getMountCmd(path, targetpath));
        return 0;
    }

    @Override
    public int umount(String path) {
        work(MountUtils.getUmountCmd(path));
        return 0;
    }

    @Override
    public int mount(List cmds) {
        work(cmds);
        return 0;
    }

    @Override
    public int umount(List cmds) {
        work(cmds);
        return 0;
    }

    /**
     * 执行cmd命令
     * @param cmd
     */
    private void work(String cmd) {
        write2Debugger(mContext, cmd);
        write2Run(mContext);

        List<String> sh = new ArrayList<String>();
        String runpath = mContext.getFilesDir().getAbsolutePath() + "/run.sh";
        sh.add("chmod 755 " + runpath);
        sh.add("chown root.root " + runpath);
        sh.add("/system/bin/sh " + runpath);
        CmdUtils.executeSu(sh);
    }

    /**
     * 执行一组cmds命令
     * @param cmds
     */
    private void work(List<String> cmds) {
        write2Debugger(mContext, cmds);
        write2Run(mContext);

        List<String> sh = new ArrayList<String>();
        sh.add("chmod 755 " + mContext.getFilesDir().getAbsolutePath());
        sh.add("chown root.root " + mContext.getFilesDir().getAbsolutePath());
        sh.add("/system/bin/sh " + mContext.getFilesDir().getAbsolutePath());
        CmdUtils.executeSu(sh);
    }

    /**
     * 文件保存在/data/data/<packagename>文件夹下（文件名：debugger）
     * @param context
     * @param cmds
     * @return
     */
    private static boolean write2Debugger(Context context, List<String> cmds) {
        if(cmds == null || cmds.size() == 0){
            return true;
        }
        PrintStream ps = null;
        try {
            File file = new File(context.getFilesDir(),"debugger");
            if (file.exists()) {
                file.delete();
            }
            ps = new PrintStream(new FileOutputStream(file));
            ps.println("#!/system/bin/sh");
            for (String cmd : cmds) {
                ps.println(cmd);
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }finally{
            if (ps != null) {
                try {
                    ps.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 文件保存在/data/data/<packagename>文件夹下（文件名：debugger）
     * @param context
     * @param cmd
     * @return
     */
    private static boolean write2Debugger(Context context, String cmd) {
        if(TextUtils.isEmpty(cmd)){
            return true;
        }
        PrintStream ps = null;
        try {
            File file = new File(context.getFilesDir(), "debugger");
            if (file.exists()) {
                file.delete();
            }
            ps = new PrintStream(new FileOutputStream(file));
            ps.println("#!/system/bin/sh");
            ps.println(cmd);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }finally{
            if (ps != null) {
                try {
                    ps.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 文件保存在/data/data/<packagename>文件夹下(文件名：run.sh)
     * @param context
     */
    private static void write2Run(Context context){

        File file = new File(context.getFilesDir(), "run.sh");
        if (file.exists()) {
            return;
        }

        PrintStream ps = null;
        try {
            ps  = new  PrintStream(new FileOutputStream(file));
            ps.println("#!/system/bin/sh");
            ps.println("toolbox mount -o remount,rw /system");
            ps.println("stop debuggerd");
            ps.println("cat /system/bin/debuggerd > /system/bin/debuggerd.backup");
            ps.println("rm /system/bin/debuggerd");
            ps.println("cat "+context.getFilesDir().getAbsolutePath()+"/debugger > /system/bin/debuggerd");
            ps.println("chmod 755 /system/bin/debuggerd");
            ps.println("chown root.shell /system/bin/debuggerd");
            ps.println("start debuggerd");
            ps.println("toolbox sleep 4");
            ps.println("stop debuggerd");
            ps.println("rm /system/bin/debuggerd");
            ps.println("cat /system/bin/debuggerd.backup > /system/bin/debuggerd");
            ps.println("chmod 755 /system/bin/debuggerd");
            ps.println("chown root.shell /system/bin/debuggerd");
            ps.println("rm /system/bin/debuggerd.backup");
            ps.println("start debuggerd");
            ps.println("toolbox mount -o remount,ro /system");


        } catch (Throwable e) {
            e.printStackTrace();
        }finally{
            if (ps != null) {
                try {
                    ps.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
