package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install.impl.CmdMount;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.impl.DirectoryManagerV1Impl;
import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.store.impl.SerializeStore;


/**
 * Created by zhulingjun on 14-3-24.
 * <p>绑定、解绑<p/>
 * @author zhulingjun
 */

public class DirectoryBind {

    private static DirectoryBind INSTANCE = null;
    private static Mount MOUNT = null;
    private static HashMap<String, MountObject> MAP = null;
    private static String SAVE_KEY = "mzw_mount_map";
    private static String SAVE_PATH = null;
    private static Context mContext = null;

    private static int MOUNT_SUCCESS = 0;

    private DirectoryBind() {
    }

    ;

    /**
     * Mount操作类
     *
     * @param context
     */
    private DirectoryBind(Context context) {
        mContext = context;
        SAVE_PATH = DirectoryManagerV1Impl.getInstance().getGlobalDirectory("mzw_mount_data").getAbsolutePath();
    }

    public static synchronized DirectoryBind getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DirectoryBind(context);
        }
        return INSTANCE;
    }

    /**
     * <p>绑定数据包路径到目标路径<p/>
     * @param path       数据包路径
     * @param targetpath 目标路径
     * @return
     * @author zhulingjun
     */
    public synchronized int bind(String path, String targetpath) {
        beforeMount();
        int result = MOUNT.mount(path, targetpath);
        afterMount();

        return result;
    }

    /**
     * <p>绑定数据包路径到目标路径（多条）<p/>
     * @param cmds 使用getMountCmd组装绑定命令
     * @return
     * @author zhulingjun
     */
    public synchronized int bind(List<String> cmds,Map<String,MountObject> map){
        beforeMount();
        int result = MOUNT.mount(cmds);
        setMap(map);
        afterMount();
        return result;
    }
    /**
     * 
     *<p>为map赋值</p><br/>
     * @since 5.0.0
     * @author xky
     * @param map2
     */
    private void setMap(Map<String, MountObject> map2) {
    	 for (Map.Entry<String, MountObject> entry : map2.entrySet()) {
    		 MAP.put(entry.getKey(), entry.getValue());
    	 }
	}
    /**
     * 
     *<p>TODO将集合中的数据存储到本地</p><br/>
     * @since ５.0.0
     * @author xky
     * @param storeMap
     */
    public void storeMap(Map<String, MountObject> storeMap){
    	 if(storeMap == null){
    		 return;
    	 }
    	 MAP = getMap();
    	 for (Map.Entry<String, MountObject> entry : storeMap.entrySet()) {
    		 MAP.put(entry.getKey(), entry.getValue());
    	 }
    	 afterMount();

    }


	/**
     * <p>获取绑定命令<p/>
     * @param path 数据包路径
     * @param targetpath 目标路径
     * @return
     * @author zhulingjun
     */
    public String getMountCmd(String path, String targetpath){
        return MountUtils.getMountCmd(path, targetpath);
    }

    /**
     * <p>获取解绑定命令<p/>
     * @param path 数据包路径
     * @return
     * @author zhulingjun
     */
    public String getUnmountCmd(String path){
        return MountUtils.getUmountCmd(path);
    }

    /**
     * <p>解绑数据包路径<p/>
     * @param path 数据包路径
     * @return
     */
    public synchronized int unbind(String path) {
        beforeMount();
        int result = MOUNT.umount(path);
        afterMount();
        return result;
    }

    /**
     * <p>解绑数据包路径<p/>
     * @param path 数据包路径
     * @param pack 数据包包名
     * @return
     */
    public synchronized int unbind(String pack,String path) {
        beforeMount();
        int result = MOUNT.umount(path);
        MAP.remove(pack);
        afterMount();
        return result;
    }
    /**
     * <p>解绑数据包路径(多条)<p/>
     * @param cmds 使用getUnmountCmd组装解绑命令
     * @return
     */
    public synchronized int unbind(List<String> cmds) {
        beforeMount();
        int result = MOUNT.umount(cmds);
        afterMount();

        return result;
    }

    /**
     * 绑定所有路径
     *
     * @return
     */
    public synchronized int bindAll() {
        beforeMount();
        if (MAP.isEmpty()) {
            return MOUNT_SUCCESS;
        }

        int result = 0;
        List<String> bindlist = new ArrayList<String>();
        try {
            for (Map.Entry<String, MountObject> entry : MAP.entrySet()) {
                MountObject value = entry.getValue();
                String path = value.getPath();
                String targetpath = value.getTargetpath();
                bindlist.add(MountUtils.getMountCmd(path, targetpath));
            }
        } catch (Throwable e) {

        }
        result = MOUNT.mount(bindlist);
        afterMount();

        return result;
    }

    /**
     * 解绑所有路径
     *
     * @return
     */
    public synchronized int unbindAll() {
        beforeMount();
        if (MAP.isEmpty()) {
            return MOUNT_SUCCESS;
        }

        int result = 0;
        List<String> unbindlist = new ArrayList<String>();
        try {
            for (Map.Entry<String, MountObject> entry : MAP.entrySet()) {
                MountObject value = entry.getValue();
                String path = value.getPath();
                unbindlist.add(MountUtils.getUmountCmd(path));
            }
        } catch (Throwable e) {

        }
        result = MOUNT.mount(unbindlist);
        afterMount();

        return result;
    }

    /**
     * 该数据包路径是否已经被绑定
     *
     * @param path
     * @return
     */
    public boolean isbind(String path) {
        MAP = getMap();

        if (MAP.isEmpty())
            return false;

        return MAP.containsKey(path);
    }

    private void beforeMount() {
        MOUNT = getMounter();
        MAP = getMap();
    }

    private void afterMount() {
        SerializeStore store = new SerializeStore(SAVE_PATH, null);
        
        store.save(MAP, SAVE_KEY);
    }

    /**
     * 获取Mount
     * <p/>
     * 17 = Android 4.2 \ 4.2.2
     */
    private Mount getMounter() {
        if (MOUNT == null) {

//            if (Build.VERSION.SDK_INT >= 17) {
//                MOUNT = new ShellMount(mContext);
//            } else {
                MOUNT = new CmdMount();
//            }
        }

        return MOUNT;
    }

    /**
     * 获取本地挂载记录
     *
     * @return
     */
    private HashMap<String, MountObject> getMap() {
        if (MAP == null) {
            SerializeStore store = new SerializeStore(SAVE_PATH, null);
            MAP = (HashMap<String, MountObject>) store.load(SAVE_KEY);
        }

        if (MAP == null || MAP.isEmpty()) {
            MAP = new HashMap<String, MountObject>();
        }

        return MAP;
    }
}