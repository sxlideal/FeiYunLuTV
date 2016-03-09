package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.impl;

import java.util.List;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.CmdUtils;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.Mount;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.MountUtils;


/**
 * Created by zhulingjun on 14-3-24.
 */
public class CmdMount implements Mount {
    /**
     * 通过获取命令行流执行挂载命令
     * getRuntime("su").exec(cmds);
     */
    public CmdMount() {
    }

    @Override
    public int mount(String path, String targetpath) {
        return CmdUtils.executeSu(MountUtils.getMountCmd(path, targetpath));
    }

    @Override
    public int umount(String path) {
        return CmdUtils.executeSu(MountUtils.getUmountCmd(path));
    }

    @Override
    public int mount(List<String> cmds) {
        return CmdUtils.executeSu(cmds);
    }

    @Override
    public int umount(List<String> cmds) {
        return CmdUtils.executeSu(cmds);
    }
}
