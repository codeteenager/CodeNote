package com.shuaijie.codenote.bean;

/**
 * Created by 姜帅杰 on 2016/2/2.
 * sqlite笔记类
 */
public class Note {
    private Integer id;
    private String title;
    private String content;
    private String time;

    public Note() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
