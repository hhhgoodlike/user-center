package com.hh.usercenter.service;

import com.hh.usercenter.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    public void testRedis(){
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //增
        valueOperations.set("hhSpring","hh");
        valueOperations.set("hhInt",1);
        valueOperations.set("hhDouble",3.0);
        User user = new User();
        user.setUsername("hhhh");
        user.setId(1L);
        valueOperations.set("hhUser",user);

        //查
        Object hh = valueOperations.get("hhSpring");
        Assert.assertTrue("hh".equals((String) hh));

        hh = valueOperations.get("hhInt");
        Assert.assertTrue(1 == (Integer) hh);

        hh = valueOperations.get("hhDouble");
        Assert.assertTrue(3.0 == (Double) hh);

        hh = valueOperations.get("hhUser");
        Assert.assertTrue(user.equals((User) hh));
        System.out.println(hh);

        redisTemplate.delete("hhUser");
    }
}
