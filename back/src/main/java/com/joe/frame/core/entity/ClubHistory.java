package com.joe.frame.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.joe.frame.core.database.AutoEntity;

import lombok.Data;

/**
 * @author:joe
 */
@Data
@Table
@Entity
public class ClubHistory extends AutoEntity {
    //用户ID
    @Column(length = 50)
    private String uid;
    //加入的俱乐部ID
    @Column(length = 50)
    private String cid;
}
