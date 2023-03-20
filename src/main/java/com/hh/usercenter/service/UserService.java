package com.hh.usercenter.service;

import com.hh.usercenter.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.hh.usercenter.constant.UserConstant.ADMIN_ROLE;

/**
 *
 * 用户服务
* @author hh
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-02-15 13:42:36
*/
public interface UserService extends IService<User> {

    /**
     *用户注册
     * @param userAccount 用户账号
     * @param password    用户密码
     * @param checkPassword     校验密码
     * @return             用户id
     */
    long userRegister(String userAccount,String password,String checkPassword,String plantCode);

    /**
     *用户登录
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

    /**
     * 用户信息修改
     * @param user
     * @return
     */
    int updateUser(User user,User loginUser);

    /**
     * 获取已登录的用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 判断是否为管理员
     * @param servletRequest
     * @return
     */
    Boolean isAdmin(HttpServletRequest servletRequest);

    /**
     * 判断是否为管理员
     * @param userLogin
     * @return
     */
    Boolean isAdmin(User userLogin );

    /**
     * 获取推荐的用户
     * @param request
     * @return
     */
    User recommendUsers(long pageSize,long pageNum,HttpServletRequest request);


    /**
     * 通过标签查询用户
     * @param tagNameList
     * @return
     */
    List<User> searchUserByTags(List<String> tagNameList);
}
