package com.hh.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hh.usercenter.model.User;
import com.hh.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 *
 * @author hh
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private List<Long> mainUserList = Arrays.asList(200003L);

    //每天凌晨0点开始执行，开始预热
    @Scheduled(cron = "0 19 16 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("hh:precachejob:docache:lock");
        try {
            if (lock.tryLock(0,30000L,TimeUnit.MILLISECONDS)){
                System.out.println("getLock:" + Thread.currentThread().getId());
                for (Long userId : mainUserList){
                    String redisKey = String.format("hh:user:recommend:%s", userId);
                    ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    //写缓存
                    try {
                        valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error",e);
                    }
                }
            }else {
                System.out.println("获取锁失败");
            }
        } catch (InterruptedException e) {
           log.error("doCacheRecommendUser error",e);
        }finally {
            //判断锁是否是当前线程的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unlock");
                lock.unlock();
            }
        }
    }
}
