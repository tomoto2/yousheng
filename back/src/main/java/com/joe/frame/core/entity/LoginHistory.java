package com.joe.frame.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.joe.frame.core.database.AutoEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author:joe
 */
@Getter
@Setter
@ToString
@Table
@Entity
public class LoginHistory extends AutoEntity {
    //登陆用户的id
    @Column(length = 50)
    private String uid;
    //登录方式，是否是微信登陆，true为是，false为账号密码登陆
    private boolean isWx;
}
