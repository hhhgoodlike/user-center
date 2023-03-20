package com.hh.usercenter.once;

import com.hh.usercenter.mapper.UserMapper;
import com.hh.usercenter.model.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;

    /**
     * 模拟插入数据至数据库中
     */
//    @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)//延迟5秒后插入数据
    public void doInsertUsers(){
        //开启计时
        StopWatch stopWatch = new StopWatch();
        System.out.println("gggg");
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假数据");
            user.setUserAccount("fakeNumber");
            user.setPassword("123456789");
            user.setEmail("456@qq.com");
            user.setAvatarUrl("");
            user.setGender(1);
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setIsDelete(0);
            user.setPlantCode("111111");
            user.setTags("[]");
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}
