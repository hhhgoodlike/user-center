package com.hh.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hh.usercenter.common.BaseResponse;
import com.hh.usercenter.common.ErrorCode;
import com.hh.usercenter.common.ResultUtils;
import com.hh.usercenter.exception.BusinessException;
import com.hh.usercenter.model.User;
import com.hh.usercenter.model.request.UserLoginRequest;
import com.hh.usercenter.model.request.UserRegisterRequest;
import com.hh.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hh.usercenter.constant.UserConstant.*;

/**
 * 用户接口
 *
 * @author hh
 */

@RestController
@RequestMapping("/user")
public class userController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userController(@RequestBody UserRegisterRequest userRegisterRequest){

        if (userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String plantCode = userRegisterRequest.getPlantCode();
        if(StringUtils.isAnyBlank(userAccount,password,checkPassword,plantCode)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        long id = userService.userRegister(userAccount, password, checkPassword,plantCode);
//        return new  BaseResponse<>(0,"OK",id);
        return ResultUtils.success(id);
    }



    @PostMapping("/login")
    public BaseResponse<User> userController(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){

        if (userLoginRequest == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = userLoginRequest.getUserAccount();
        String password = userLoginRequest.getPassword();
        if(StringUtils.isAnyBlank(userAccount,password)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, password,request);
//        return new BaseResponse<>(0,"OK",user);
        return ResultUtils.success(user);
    }


    @PostMapping("/logout")
    public BaseResponse<Integer> userLogOut( HttpServletRequest request){
        if (request == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        int i = userService.userLogOut(request);
        return ResultUtils.success(i);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object objUser = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) objUser;
        if (currentUser == null){
            return ResultUtils.error(ErrorCode.NOT_LOGIN);
        }
        Long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }


    @GetMapping("/search")
    public BaseResponse<List<User>> SearchUser(String username, HttpServletRequest servletRequest){

        if(!isAdmin(servletRequest)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不是管理员");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> users = userService.list(queryWrapper);

        List<User> collect = users.stream().map(user -> {
            return userService.getSafetyUser(user);
        }).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest servletRequest){
        if(!isAdmin(servletRequest)){
            return null;
        }
        if (id <= 0){
            return null;
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);

    }

    /**
     * 判断是否为管理员
     * @param servletRequest
     * @return
     */
    private boolean isAdmin(@RequestBody HttpServletRequest servletRequest){
        Object userObject = servletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User)userObject;
        if(user == null || user.getUserRole() != ADMIN_ROLE){
            return false;
        }
        return true;
     }
}
