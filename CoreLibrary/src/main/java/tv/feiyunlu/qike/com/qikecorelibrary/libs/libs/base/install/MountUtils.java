package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

/**
 * Created by zhulingjun on 14-3-25.
 */
public class MountUtils {
    /**
     * 返回挂载命令
     * @param path 路径
     * @param targetPath 目标路径
     * @return
     */
    public static String getMountCmd(String path, String targetPath){
        return "mount -o bind " + targetPath + " " + path;
    }

    /**
     * 返回解绑挂载命令
     * @param path 路径
     * @return
     */
    public static String getUmountCmd(String path){
        return "umount " + path;
    }
}
