package com.hh.usercenter.service;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.hh.usercenter.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("hh");
        user.setUserAccount("123");
        user.setPassword("123456");
        user.setEmail("187096");
        user.setAvatarUrl("https://baike.baidu.com/pic/%E6%96%AD%E8%A8%80/13021995/1/4610b912c8fcc3ceffdb78b99845d688d43f2024?fr=lemma&fromModule=lemma_top-image&ct=single#aid=1&pic=4610b912c8fcc3ceffdb78b99845d688d43f2024");
        Boolean result =  userService.save(user);
        System.out.println(user.getId());
        Assertions.assertEquals(true,result);
    }

    @Test
    public void testSearchUserByTags(){
        List<String> tagNameList = Arrays.asList("java","Python");
        List<User> userList = userService.searchUserByTags(tagNameList);
        Assert.assertNotNull(userList);
    }

    @Test
    void userRegister() {
        String userAccount = "aahhh";
        String password = "123456789";
        String checkPassword = "123456789";
        String plantCode = "1243";
        long result = userService.userRegister(userAccount,password,checkPassword,plantCode);
//        Assertions.assertEquals(-1,result);
        System.out.println("成功");
//        userAccount = "hhhhh";
//        password = "123456789";
//        long result = userService.userRegister(userAccount,password,checkPassword,plantCode);
    }
}