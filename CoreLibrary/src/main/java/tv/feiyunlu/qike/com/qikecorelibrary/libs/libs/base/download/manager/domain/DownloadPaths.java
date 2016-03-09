package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.download.manager.domain;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class DownloadPaths implements Parcelable , Serializable
{

    private static final long serialVersionUID = 1L;
    
    //下载方式ID，服务器定义，唯一
    private int               id;
    
    //下载方式名称，服务器定义
    private String            name;
    
    //下载方式图标，服务器定义
    private String            icon;
    
    //下载URL
    private String            url;
    // 备用的下载地址，网盘方式下载必须设置
    private String            backup;
    
    //是否在客户端可见，0为不可见，1为可见
    private int               visible;
    
    
    //是否需要进行解析，网盘方式必须设置为true
    private boolean          parse;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public int getVisible(){
        return visible;
    }

    public void setVisible(int visible){
        this.visible = visible;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getIcon(){
        return icon;
    }

    public void setIcon(String icon){
        this.icon = icon;
    }

    public String getBackup(){
        return backup;
    }

    public void setBackup(String backup){
        this.backup = backup;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest,int flags){

    }

    
    public boolean isParse(){
        return parse;
    }

    
    public void setParse(boolean parse){
        this.parse = parse;
    }
    
}
