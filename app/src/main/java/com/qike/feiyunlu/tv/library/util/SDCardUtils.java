package com.qike.feiyunlu.tv.library.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * sd 工具类 1、获取外置SD卡路径 2、获取内置SD卡路径 3、获得ROM空间路径 4、判断是否是同一个空间（外置SD卡和内置SD卡） 注：getFreeSpace()需要2.3系统版本 反射getVolumeList()需要4.0以上版本
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class SDCardUtils
{

    private static String                        IN_SD                              = "in_sd";
    private static String                        EX_SD                              = "ex_sd";
    private static int                           IN_SD_ID                           = 65537;
    private static int                           EX_SD_ID                           = 131073;
    private static Map<String, StorageDriectory> voldList;
    /**
     * getVolumeList通过反射获取vlod设备列表。需要4.0版本以上。
     */
    public static int                            MIN_APILEVEL_GET_VOLUMELIST        = 14;
    /**
     * getFreeSpace()、getTotalSpace()需要2.3版本以上。
     */
    public static int                            MIN_APILEVEL_GET_SPACE             = 9;
    /**
     * ROM空间和内置存储空间大小不超过这个值，只显示内置存储空间 0.2G
     */
    public static long                           VOLUME_ABS_BETWEEN_ROM_AND_INNERSD = 500 * 1000 * 1000;
    private static boolean                       have2SpaceAndDefaultSdIsExsd;

    /**
     * 判断内置存储卡和ROM空间是否是同一个
     * 
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean isRomEqualsInnersd(Context context)
    {
        File rom = getROMDirectory ();
        StorageDriectory inner = getInnerStorageDirectory (context);

        if (inner != null)
        {
            long romTotalSpace = rom.getTotalSpace ();
            long innerTotalSpace = inner.getTotal ();

            if (Math.abs (romTotalSpace - innerTotalSpace) < VOLUME_ABS_BETWEEN_ROM_AND_INNERSD) return true;
        }
        return false;
    }

    /**
     * 根据Google隐藏API获取双存储卡空间大小 通过以下三个步骤获取 1 判断storageId，如果是65537-》内置，131073-》外置 2 判断是否进行了内外置SD卡切换，像华为手机 3 【废弃】通过Environment.getExternalStorageDirectory()判断voldList中的in_sd是否和其路径一样，如果不一样，就置换in_sd和ex_sd
     * 但是第三条明显被废弃了，因为红米手机有两个空间，但是通过Environment.getExternalStorageDirectory()获取的是外置SD卡的空间，所以获取方式不准
     * 
     * @param context
     * @return
     */
    @TargetApi(14)
    public static Map<String, StorageDriectory> initStorageVolume(Context context)
    {
        voldList = new HashMap<String, StorageDriectory> ();
        try
        {
            StorageManager sm = (StorageManager) context.getSystemService (Context.STORAGE_SERVICE);
            Method method = sm.getClass ().getDeclaredMethod ("getVolumeList");
            method.setAccessible (true);

            StorageDriectory insd = null;
            StorageDriectory exsd = null;
            Object[] array = (Object[]) method.invoke (sm);
            for ( Object obj : array )
            {
                Field mStorageIdField = obj.getClass ().getDeclaredField ("mStorageId");
                Field mRemovableField = obj.getClass ().getDeclaredField ("mRemovable");
                Field mPathField = obj.getClass ().getDeclaredField ("mPath");
                mStorageIdField.setAccessible (true);
                mRemovableField.setAccessible (true);
                mPathField.setAccessible (true);

                Integer storageId = (Integer) mStorageIdField.get (obj);
                Method m = obj.getClass ().getMethod ("toString");
                m.setAccessible (true);

                if (storageId == IN_SD_ID || storageId == EX_SD_ID)
                {
                    Boolean removable = (Boolean) mRemovableField.get (obj);
                    String path;
                    if (mPathField.get (obj) instanceof File)
                    {
                        path = ((File) mPathField.get (obj)).getPath ();
                    } else
                    {
                        path = (String) mPathField.get (obj);
                    }

                    if (!isPathExists (path)) break;

                    // remove 判断不一定准确
                    // if (removable) {
                    // storageDriectory.setPath(path);
                    // storageVolumeList.put(NAME_EXTERNAL_STORAGE_DIRECTORY, storageDriectory);
                    // } else {
                    // storageVolumeList.put(NAME_INNER_STORAGE_DIRECTORY, storageDriectory);
                    // }

                    if (storageId == EX_SD_ID)
                    {
                        exsd = new StorageDriectory ();
                        exsd.setPath (path);
                        exsd.setRemovable (removable);
                        exsd.setStorageId (storageId);
                        voldList.put (EX_SD, exsd);
                    } else if (storageId == IN_SD_ID)
                    {
                        insd = new StorageDriectory ();
                        insd.setPath (path);
                        insd.setRemovable (removable);
                        insd.setStorageId (storageId);
                        voldList.put (IN_SD, insd);
                    }
                }
            }
            
            if (exsd != null && insd != null && !exsd.removable && insd.removable)
            {
                have2SpaceAndDefaultSdIsExsd = true;
                voldList.put (EX_SD, insd);
                voldList.put (IN_SD, exsd);
            } else
            {
                have2SpaceAndDefaultSdIsExsd = false;
            }

            // checkExsdAndEnvironmentsd(voldList, context);
        } catch (Throwable e)
        {
            e.printStackTrace ();
            return null;
        }
        return voldList;
    }

    /**
     * applist反射出来的expath和Environment.getex。。获取的外置sd路径文件大小是否一致 有些国产手机获取到的内外置路径会颠倒
     * 
     * @param liMap
     * @param context
     */
    private static void checkExsdAndEnvironmentsd(Map<String, StorageDriectory> liMap,Context context)
    {
        StorageDriectory ex = liMap.get (EX_SD);
        StorageDriectory in = liMap.get (IN_SD);
        File listexf = null;
        File listinf = null;
        File enviexf = null;

        if (ex != null && !TextUtils.isEmpty (ex.getPath ()))
        {
            listexf = new File (ex.getPath ());
        }

        if (in != null && !TextUtils.isEmpty (in.getPath ()))
        {
            listinf = new File (in.getPath ());
        }

        if (Environment.getExternalStorageState ().equals (Environment.MEDIA_MOUNTED))
        {
            enviexf = Environment.getExternalStorageDirectory ();
        }

        if (have2Space (context) && (listinf.getTotalSpace () != enviexf.getTotalSpace ()) && (listexf.getTotalSpace () == enviexf.getTotalSpace ()))
        {
            voldList.put (EX_SD, in);
            voldList.put (IN_SD, ex);
        }
    }

    /**
     * 判断该路径是否存在
     * 
     * @param path
     * @return
     */
    private static boolean isPathExists(String path)
    {
        File file = new File (path);
        if (!file.exists ())
        {
            return false;
        } else
        {
            return true;
        }
    }

    // ======================================================================
    // 获取ROM空间大小
    // ======================================================================
    /** 获取rom空间 **/
    public static File getROMDirectory()
    {
        return Environment.getDataDirectory ();
    }

    /**
     * <p>
     * 清空存储册
     * </p>
     * <br/>
     * 
     * @since 4.2
     * @author sunxh
     */
    public static void storageClear()
    {
        if (voldList != null)
        {
            voldList.clear ();
        }
    }

    // ======================================================================
    // 获取内置存储空间大小
    // ======================================================================
    /**
     * 获取内置SD卡空间
     * 
     * @param context
     **/
    @TargetApi(14)
    private static StorageDriectory getInnerStorageDirectory(Context context)
    {
        if (voldList == null || voldList.size () == 0)
        {
            initStorageVolume (context);
        }

        StorageDriectory innnerSD = voldList.get (IN_SD);
        if (innnerSD != null && !TextUtils.isEmpty (innnerSD.getPath ()))
        {
            File file = new File (innnerSD.getPath ());
            innnerSD.setUsed (file.getTotalSpace () - file.getFreeSpace ());
            innnerSD.setUnused (file.getFreeSpace ());
            innnerSD.setTotal (file.getTotalSpace ());

        }
        return innnerSD;
        // else {
        // File storageDirectory = Environment.getExternalStorageDirectory();
        // innnerSD = new StorageDriectory();
        // innnerSD.setUsed(storageDirectory.getTotalSpace() - storageDirectory.getFreeSpace());
        // innnerSD.setUnused(storageDirectory.getFreeSpace());
        // innnerSD.setTotal(storageDirectory.getTotalSpace());
        // return innnerSD;
        // }

    }

    /**
     * <p>
     * 获取内置SD卡空间
     * </p>
     * <br/>
     * 
     * @since 4.2
     * @author sunxh
     * @param context
     * @param isObtain
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public static StorageDriectory getInnerStorageDirectory(Context context,boolean isObtain)
    {
        String version = VERSION.SDK;
        int versionInt = Integer.parseInt (version);
        if (versionInt >= 14)
        {
            return getInnerStorageDirectory (context);
        } else
        {
            StorageDriectory innnerSD = new StorageDriectory ();
            File file = new File (Environment.getExternalStorageDirectory ().getPath ());
            innnerSD.setPath (Environment.getExternalStorageDirectory ().getPath ());
            innnerSD.setUsed (file.getTotalSpace () - file.getFreeSpace ());
            innnerSD.setUnused (file.getFreeSpace ());
            innnerSD.setTotal (file.getTotalSpace ());
            return innnerSD;
        }

    }

    // ======================================================================
    // 获取外置SD卡空间大小
    // ======================================================================
    /**
     * <p>
     * 获取外置SD卡空间
     * </p>
     * <br/>
     *
     * @since 4.2
     * @author sunxh
     * @param context
     * @param isObtain
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public static StorageDriectory getExStorageDirectory(Context context,boolean isObtain)
    {
        String version = VERSION.SDK;
        int versionInt = Integer.parseInt (version);
        if (versionInt >= 14)
        {
            return getExStorageDirectory (context);
        } else
        {
            StorageDriectory innnerSD = new StorageDriectory ();
            File file = new File (Environment.getExternalStorageDirectory ().getPath ());
            innnerSD.setPath (Environment.getExternalStorageDirectory ().getPath ());
            innnerSD.setUsed (file.getTotalSpace () - file.getFreeSpace ());
            innnerSD.setUnused (file.getFreeSpace ());
            innnerSD.setTotal (file.getTotalSpace ());
            return innnerSD;
        }

    }

    /** 获取外置SD卡空间 **/
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    private static StorageDriectory getExStorageDirectory(Context context)
    {
        File file = null;
        if (voldList == null || voldList.size () == 0)
        {
            if (VERSION.SDK_INT >= MIN_APILEVEL_GET_VOLUMELIST)
            {
                initStorageVolume (context);
            } else
            {
                // donothing
            }
        }

        if (VERSION.SDK_INT >= MIN_APILEVEL_GET_VOLUMELIST)
        {
            StorageDriectory exSD = voldList.get (EX_SD);
            if (exSD != null && !TextUtils.isEmpty (exSD.getPath ()))
            {
                // return ( new File(exSD.getPath()) );
                file = new File (exSD.getPath ());
            } else
            {
                // return null;
                file = null;
            }
        } else
        {
            // return Environment.getExternalStorageDirectory();
            file = Environment.getExternalStorageDirectory ();
        }

        if (file != null && file.getTotalSpace () != 0)
        {// 如果path是mnt/usbdrive/这种进不去的权限，可以通过判断file.getTotalSpace=0来排除，因为没有权限返回的total=0
            StorageDriectory sDriectory = new StorageDriectory ();
            sDriectory.setPath (file.getAbsolutePath ());
            sDriectory.setTotal (file.getTotalSpace ());
            sDriectory.setUnused (file.getFreeSpace ());
            sDriectory.setUsed (file.getTotalSpace () - file.getFreeSpace ());
            return sDriectory;
        } else
        {
            return null;
        }
    }

    /** 获取外置SD卡空间状态 **/
    public static String getExStorageDirectoryState(Context context)
    {
        if (voldList == null || voldList.size () == 0)
        {
            if (VERSION.SDK_INT >= MIN_APILEVEL_GET_VOLUMELIST)
            {
                initStorageVolume (context);
            } else
            {
                // donothing
            }
        }

        if (VERSION.SDK_INT >= MIN_APILEVEL_GET_VOLUMELIST)
        {
            return voldList.get (EX_SD).getPath ();
        } else
        {
            return Environment.getExternalStorageState ();
        }
    }

    /**
     *  判断是否是双存储卡
     * 
     * @param context
     * @return
     */
    public static boolean have2Space(Context context)
    {
        if (VERSION.SDK_INT >= MIN_APILEVEL_GET_VOLUMELIST)
        {
            StorageDriectory exStorageDirectory = getExStorageDirectory (context);
            StorageDriectory innerDirectory = getInnerStorageDirectory (context);
            if (exStorageDirectory != null && innerDirectory != null)
            {
                return true;
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public static boolean isSDCardMouted()
    {
        try
        {
            String state = Environment.getExternalStorageState ();
            return state.equals (Environment.MEDIA_MOUNTED);
        } catch (Exception e)
        {
            e.printStackTrace ();
        }
        return false;
    }

    /**
     * 判断外置SD卡是否是默认的SD卡
     * 
     * @return
     */
    public static boolean have2SpaceAndDefaultSdIsExsd(Context context)
    {
        return have2SpaceAndDefaultSdIsExsd;
    }

    /**
     * 置换内外置sd卡
     * 
     * @param context
     */
    public static void changeExsdAndInsd(Context context)
    {
        if (voldList != null && voldList.size () >= 2)
        {
            StorageDriectory ex = getExStorageDirectory (context, true);
            StorageDriectory in = getInnerStorageDirectory (context, true);
            voldList.put (EX_SD, in);
            voldList.put (IN_SD, ex);
        }
    }

    /**
     * 设备实体类
     * 
     * @author zhulingjun
     */
    public static class StorageDriectory
    {

        private boolean removable;

        public boolean isRemovable()
        {
            return removable;
        }

        public void setRemovable(boolean removable)
        {
            this.removable = removable;
        }

        public String getPath()
        {
            return path;
        }

        public void setPath(String path)
        {
            this.path = path;
        }

        public int getStorageId()
        {
            return storageId;
        }

        public void setStorageId(int storageId)
        {
            this.storageId = storageId;
        }

        public long getTotal()
        {
            return total;
        }

        public void setTotal(long total)
        {
            this.total = total;
        }

        public long getUsed()
        {
            return used;
        }

        public void setUsed(long used)
        {
            this.used = used;
        }

        public long getUnused()
        {
            return unused;
        }

        public void setUnused(long unused)
        {
            this.unused = unused;
        }

        private String path;
        private int    storageId;
        private long   total;
        private long   used;
        private long   unused;
    }
}
