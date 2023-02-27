package com.hh.usercenter.service;

import com.hh.usercenter.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * 用户服务
* @author hh
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-02-15 13:42:36
*/
public interface UserService extends IService<User> {



    /**
     *
     * @param userAccount 用户账号
     * @param password    用户密码
     * @param checkPassword     校验密码
     * @return             用户id
     */
    long userRegister(String userAccount,String password,String checkPassword,String plantCode);


    /**
     *
     * @param userAccount  登录账号
     * @param password     登录密码
     * @param request
     * @return             User
     */
    User userLogin(String userAccount, String password, HttpServletRequest request);


    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     *用户注销
     * @param request
     * @return
     */
    int userLogOut(HttpServletRequest request);
}
