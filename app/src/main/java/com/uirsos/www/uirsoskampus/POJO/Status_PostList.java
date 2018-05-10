package com.uirsos.www.uirsoskampus.POJO;

import java.util.Date;

/**
 * Created by cunun12 on 05/05/2018.
 */

public class Status_PostList extends PostId{

    private String user_id, desc, image_thumb, image_post;
    private String Timestamp;

    public Status_PostList() {
    }

    public Status_PostList(String user_id, String desc, String image_thumb, String image_post, String timestamp) {
        this.user_id = user_id;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.image_post = image_post;
        this.Timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getImage_post() {
        return image_post;
    }

    public void setImage_post(String image_post) {
        this.image_post = image_post;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }
}
