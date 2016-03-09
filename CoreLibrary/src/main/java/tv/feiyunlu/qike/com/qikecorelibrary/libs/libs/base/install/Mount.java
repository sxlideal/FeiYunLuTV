package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

import java.util.List;

/**
 * Created by zhulingjun on 14-3-24.
 */
public interface Mount {
    /**
     * 挂载
     * mount [-r] [-w] [-o options] [-t type] device directory
     * @param path
     * @param targetpath
     * @return
     */
    int mount(String path, String targetpath);

    /**
     * 取消挂载
     * umount path
     * @param path
     * @return
     */
    int umount(String path);

    int mount(List<String> cmds);

    int umount(List<String> cmds);
}
