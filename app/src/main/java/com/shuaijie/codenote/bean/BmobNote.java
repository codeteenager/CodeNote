package com.shuaijie.codenote.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 姜帅杰 on 2016/2/9.
 * 后台笔记类
 */
public class BmobNote extends BmobObject {
    private String title;
    private String content;
    private String time;
    private String userId;
    private String localId;

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
