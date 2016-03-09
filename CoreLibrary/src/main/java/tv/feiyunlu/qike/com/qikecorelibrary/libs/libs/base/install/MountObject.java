package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.install;

import java.io.Serializable;

/**
 * Created by zhulingjun on 14-3-25.
 */
public class MountObject implements Serializable{
    /**
	 * TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 5404890200159469259L;
	// path    targetpath    timestamp    status
    /**
     * 路径
     */
    String path;
    /**
     * 目标路径
     */
    String targetpath;
    /**
     * 时间戳
     */
    long timestamp;
    /**
     * 状态码
     */
    int status;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTargetpath() {
        return targetpath;
    }

    public void setTargetpath(String targetpath) {
        this.targetpath = targetpath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
