package com.hh.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hh.usercenter.common.ErrorCode;
import com.hh.usercenter.exception.BusinessException;
import com.hh.usercenter.service.UserService;
import com.hh.usercenter.model.User;
import com.hh.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hh.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author hh
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-02-15 13:42:36
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {


    @Resource
    private UserMapper userMapper;


    /**
     *  盐值,混淆密码
     */
    private static final String SALT = "hh";


    @Override
    public long userRegister(String userAccount, String password, String checkPassword,String plantCode) {
        //1. 校验
        if(StringUtils.isAnyBlank(userAccount,password,checkPassword,plantCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }

        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度过短");
        }

        if (password.length() < 8 || checkPassword.length() <8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度过短");
        }

        if (plantCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编码不正确");
        }

        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\s]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号有特殊字符");
        }

        //密码和校验码密码相同
        if(!password.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验码密码不同");
        }

        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }

        //星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plantCode",plantCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号已使用");
        }

        //2. 加密
        String encrypPassword = DigestUtils.md5DigestAsHex((SALT+password).getBytes(StandardCharsets.UTF_8));

        //3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPassword(encrypPassword);
        user.setPlantCode(plantCode);
        Boolean resultSave = this.save(user);
        if (!resultSave){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"保存失败");
        }
        return user.getId();

    }

    @Override
    public User userLogin(String userAccount, String password, HttpServletRequest request) {
        //1. 校验
        if(StringUtils.isAnyBlank(userAccount,password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码不存在");
        }

        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于4");
        }

        if (password.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度小于8");
        }

        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\s]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号有特殊符号");
        }

        //2. 加密
        String encrypPassword = DigestUtils.md5DigestAsHex((SALT+password).getBytes(StandardCharsets.UTF_8));
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("password",encrypPassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            log.info("user login failed,userAccount cannot match password");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码错误");
        }

        //3. 脱敏
        User safetyUser = getSafetyUser(user);

        //4.记录用户的登录态：
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if(originUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码错误");
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setPlantCode(originUser.getPlantCode());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public int userLogOut(HttpServletRequest request) {
        //移除Session中的登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;

    }

    /**
     * 通过标签查询用户（内存过滤）
     * @param tagNameList
     * @return
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNameList) {

        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //内存查询
        //1.查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> usersList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2.在内存中判断是否有匹配的标签
        return usersList.stream().filter(user -> {
            String tagsStr = user.getTags();
            if(StringUtils.isAnyBlank(tagsStr)){
                return false;
            }
            Set<String> tempTagsName = gson.fromJson(tagsStr,new TypeToken<Set<String>>(){}.getType());
            //判断tempTagsName是否为空:Optional.ofNullable(tempTagsName)判断是否为空，空的话，执行new HashSet<>()
            tempTagsName = Optional.ofNullable(tempTagsName).orElse(new HashSet<>());
            for (String tagsName : tagNameList){
                if(!tempTagsName.contains(tagsName)){
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * 通过标签查询用户（SQL查询版）
     * @param tagNameList
     * @return
     */
    @Deprecated
    private List<User> searchUserByTagsBySql(List<String> tagNameList) {

        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //sql语句查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //拼接and查询
        //like '%java%' and like '%Python%'
        for (String tagName : tagNameList){
            queryWrapper = queryWrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());



    }
}




