package tv.feiyunlu.qike.com.qikecorelibrary.libs.libs.base.datainterface.impl;

import java.util.List;



public class ResultEntity<T> {

    private T t;
    private List<T> list;
    

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    
    
    
}
