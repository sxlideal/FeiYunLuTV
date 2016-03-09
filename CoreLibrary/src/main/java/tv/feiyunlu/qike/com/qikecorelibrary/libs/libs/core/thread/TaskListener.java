/*
 * 文件名：TaskListener.java
 * 描述：任务监听器
 * 版本：v1.0.0
 * 日期：2014-3-15
 * 版权：Copyright ? 2014 北京富邦展瑞科技有限公司 All rights reserved.
 */
package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.core.thread;


/**
 *<p>任务监听器</p><br/>
 *<p>包含了任务开始，结束，取消，进度条更新</p>
 * @since 1.0.0
 * @author suenxianhao
 *
 */
public interface TaskListener<Progress, Result> {
	

	/**
	 *<p>任务执行前的准备工作</p><br/>
	 * @since 1.0.0
	 * @author suenxianhao
	 */
	public void onTaskBegin();

	/**
	 *<p>任务结束后的处理</p><br/>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param result
	 */
	public void onTaskEnd(Result datas);

	/**
	 *<p>任务取消的处理</p><br/>
	 * @since 1.0.0
	 * @author suenxianhao
	 */
	public void onTaskCancelled();

	/**
	 *<p>更新进度信息</p><br/>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param values
	 */
	public void onProgressUpdate(Progress... values);

	/**
	 *<p>任务失败的处理</p><br/>
	 * @since 1.0.0
	 * @author suenxianhao
	 * @param failed 分为普通失败和网络超时的失败
	 * @see TaskFailed
	 */
	public void onTaskFailed(Task.TaskFailed failed);

}
