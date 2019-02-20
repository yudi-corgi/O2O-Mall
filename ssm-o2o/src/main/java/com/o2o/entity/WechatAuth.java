package com.o2o.entity;

import java.util.Date;

/*
    微信号信息
 */
public class WechatAuth {

    //微信账号id
    private Long wechatAuthId;
    //微信号与公众号绑定的唯一标识ID
    private String openId;
    //创建时间
    private Date createTime;
    //主外键关联-->用户信息表的userId
    private PersonInfo personInfo;

    public Long getWechatAuthId() {
        return wechatAuthId;
    }

    public void setWechatAuthId(Long wechatAuthId) {
        this.wechatAuthId = wechatAuthId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public PersonInfo getPersonInfo() {
        return personInfo;
    }

    public void setPersonInfo(PersonInfo personInfo) {
        this.personInfo = personInfo;
    }
}
