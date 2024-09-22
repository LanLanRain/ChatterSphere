package com.lb.live.user.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author RainSoul
 * @create 2024-09-22
 */
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String nickName;
    private String trueName;
    private String avatar;
    private Integer sex;
    private Integer workCity;
    private Integer bornCity;
    private Date bornDate;
    private Date createTime;
    private Date updateTime;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getBornCity() {
        return bornCity;
    }

    public void setBornCity(Integer bornCity) {
        this.bornCity = bornCity;
    }

    public Date getBornDate() {
        return bornDate;
    }

    public void setBornDate(Date bornDate) {
        this.bornDate = bornDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getWorkCity() {
        return workCity;
    }

    public void setWorkCity(Integer workCity) {
        this.workCity = workCity;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "avatar='" + avatar + '\'' +
                ", userId=" + userId +
                ", nickName='" + nickName + '\'' +
                ", trueName='" + trueName + '\'' +
                ", sex=" + sex +
                ", workCity=" + workCity +
                ", bornCity=" + bornCity +
                ", bornDate=" + bornDate +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
