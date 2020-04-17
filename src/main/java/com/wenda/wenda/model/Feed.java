
package com.wenda.wenda.model;

import com.alibaba.fastjson.JSONObject;

import javax.xml.crypto.Data;
import java.util.Date;

public class Feed {
    private int id;
    private int type;
    private int userId;
    private Date createdDate;

    private String data;
    private JSONObject datajson = null;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreateDate() {
        return createdDate;
    }

    public void setCreateDate(Date createDate) {
        this.createdDate = createDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        datajson = JSONObject.parseObject(data);
    }

    public String get(String key){
        return datajson == null ? null:datajson.getString(key);
    }

}
