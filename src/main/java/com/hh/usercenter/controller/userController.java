package com.hh.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hh.usercenter.common.BaseResponse;
import com.hh.usercenter.common.ErrorCode;
import com.hh.usercenter.common.ResultUtils;
import com.hh.usercenter.exception.BusinessException;
import com.hh.usercenter.model.User;
import com.hh.usercenter.model.request.UserLoginRequest;
import com.hh.usercenter.model.request.UserRegisterRequest;
import com.hh.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hh.usercenter.constant.UserConstant.*;

/**
 * 用户接口
 *
 * @author hh
 */

@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5173"},allowCredentials = "true")
@RequestMapping("/user")
@Slf4j
public class userController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){

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


    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){

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

    /**
     * 注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogOut( HttpServletRequest request){
        if (request == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        int i = userService.userLogOut(request);
        return ResultUtils.success(i);
    }

    /**
     * 获取Session中的用户信息
     * @param request
     * @return
     */
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


    /**
     * 查找用户
     * @param username
     * @param servletRequest
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> SearchUser(String username, HttpServletRequest servletRequest){

        if(!userService.isAdmin(servletRequest)){
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

    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize,long pageNum, HttpServletRequest servletRequest){
        User loginUser = userService.getLoginUser(servletRequest);
        String redisKey = String.format("yupao:user:recommend:%s", loginUser.getId());
        ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
        //有缓存，直接读取
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null){
            return ResultUtils.success(userPage);
        }
        //无缓存，从数据库中查询，并将数据缓存到redis中
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum,pageSize),queryWrapper);
        try {
            valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error",e);
        }
        return ResultUtils.success(userPage);
    }

    /**
     * 通过标签查找用户
     * @param tagNameList
     * @return
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUserByTags(tagNameList);
//        Gson gson = new Gson();
//        userList.forEach(user -> {
//            System.out.println(user.getTags());
//            String tempTagsName = gson.fromJson(user.getTags(), String.class);
//            System.out.println(tempTagsName);
//            user.setTags(tempTagsName);
//            System.out.println(user.getTags());
//        });
        return ResultUtils.success(userList);
    }

    /**
     * 修改用户信息
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        //1.判断参数是否为空
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.判断权限
        User loginUser = userService.getLoginUser(request);
        //3.修改用户信息
        Integer integer = userService.updateUser(user,loginUser);
        return ResultUtils.success(integer);
    }

    /**
     * 移除用户
     * @param id
     * @param servletRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest servletRequest){
        if(!userService.isAdmin(servletRequest)){
            return null;
        }
        if (id <= 0){
            return null;
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);

    }


}
