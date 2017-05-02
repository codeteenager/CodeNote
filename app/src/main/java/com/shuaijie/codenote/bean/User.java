package com.shuaijie.codenote.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by 姜帅杰 on 2016/2/8.
 * 后台用户类
 */
public class User extends BmobUser {
    private String fileName;
    private String fileUrl;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
