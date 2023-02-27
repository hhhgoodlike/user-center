package com.hh.usercenter.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 用于注册邮箱或者登录账号
     */
    private String email;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 0-正常  1-封号
     */
    private Integer userStatus;

    /**
     * 0-普通用户  1-管理员
     */
    private Integer userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改日期
     */
    private Date updateTime;

    /**
     * 0-正常  1-删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 星球编号
     */
    private String plantCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}