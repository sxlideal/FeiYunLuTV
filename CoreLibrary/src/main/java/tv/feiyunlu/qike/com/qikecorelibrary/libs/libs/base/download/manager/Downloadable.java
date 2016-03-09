package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager;


import tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.domain.DownloadPaths;

public interface Downloadable {

	public static final int TYPE_DOWNLOAD = -1;
	public static final int TYPE_HISTORY = -2;

	
	/**
	 * 
	 *<p>返回包名</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public String getPackagename();
	
	
	/**
	 * 
	 *<p>设置包名</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param packagename
	 */
	public void setPackagename(String packagename);
	
	
	/**
	 * 
	 *<p>返回任务类型</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 返回任务类型
	 */
	public int getTaskType();

	
	/**
	 * 
	 *<p>设置任务类型</p><br/>
	 *<p>由DownloadManager调用</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param type 类型
	 */
	public void setTaskType(int type);

	
	/**
	 * 
	 *<p>获取保存路径</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 返回保存路径
	 */
	public String getSavePath();

	
	
	/**
	 * 
	 *<p>设置保存路径</p><br/>
	 *<p>由DownloadManager调用</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param savePath 保存路径
	 */
	public void setSavePath(String savePath);

	
	
	/**
	 * 
	 *<p>获取下载URL</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 返回下载地址
	 */
	public DownloadPaths getRequestPath();

	
	/**
	 * 
	 *<p>设置下载地址</p><br/>
	 *<p>只有开启一个新下载任务前才必须进行设置，暂停后继续下载的情况下不需要设置</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param paths
	 */
	public void setRequestPath(DownloadPaths paths);

	
	
	/**
	 * 
	 *<p>获取文件总大小</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 返回文件总大小
	 */
	public long getContentLength();

	
	/**
	 * 
	 *<p>设置文件总大小</p><br/>
	 *<p>由DownloadManager调用</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param length 大小
	 */
	public void setContentLength(long length);

	
	/**
	 * 
	 *<p>获取下载唯一标记</p><br/>
	 *<p>相当于数据表主键</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return 返回唯一标记
	 */
	public String getDownloadKey();

	
	/**
	 * 
	 *<p>设置下载唯一标记</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param key 唯一标记
	 */
	public void setDownloadKey(String key);
	
	
	/**
	 * 
	 *<p>设置下载开始时间</p><br/>
	 *<p>由DownloadManager调用</p>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @param time 开始时间
	 */
	public void setDownloadStartTime(long time);
	
	/**
	 * 
	 *<p>返回下载开始时间</p><br/>
	 * @since 1.0.0
	 * @author xujiaoyong
	 * @return
	 */
	public long getDownloadStartTime();

}
