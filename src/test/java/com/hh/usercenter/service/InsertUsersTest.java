package com.hh.usercenter.service;

import com.hh.usercenter.mapper.UserMapper;
import com.hh.usercenter.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class InsertUsersTest {
    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(60,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(1000));

    @Test
    public void testInsertUsers(){
        //开启计时
        StopWatch stopWatch = new StopWatch();
        System.out.println("gggg");
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假数据");
            user.setUserAccount("fakeNumber");
            user.setPassword("123456789");
            user.setEmail("456@qq.com");
            user.setAvatarUrl("D:\\Image\\bird.jpg");
            user.setGender(1);
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setIsDelete(0);
            user.setPlantCode("111111");
            user.setTags("[]");
//            userMapper.insert(user);
            userList.add(user);
        }
        userService.saveBatch(userList,10000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    public void testInsertUsersOne(){
        //开启计时
        StopWatch stopWatch = new StopWatch();
        System.out.println("gggg");
        stopWatch.start();
        final int INSERT_NUM = 100000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<User> userList = new ArrayList<>();
            while (true){
                j++;
                User user = new User();
                user.setUsername("假数据");
                user.setUserAccount("fakeNumber");
                user.setPassword("123456789");
                user.setEmail("456@qq.com");
                user.setAvatarUrl("D:\\Image\\bird.jpg");
                user.setGender(1);
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setIsDelete(0);
                user.setPlantCode("111111");
                user.setTags("[]");
                userList.add(user);
                if (j % 10000 == 0){
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName" + Thread.currentThread().getName());
                userService.saveBatch(userList,10000);
            },executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
