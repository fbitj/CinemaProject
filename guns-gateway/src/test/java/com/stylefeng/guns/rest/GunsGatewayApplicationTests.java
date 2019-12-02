package com.stylefeng.guns.rest;

import com.stylefeng.guns.rest.common.persistence.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GunsGatewayApplicationTests {
	@Autowired
	RedisTemplate redisTemplate;

	@Test
	public void contextLoads() {
		User user = new User();
		user.setId(1L);
		user.setUserName("哈哈");


		redisTemplate.opsForValue().set("user",user);

		User user1 = (User) redisTemplate.opsForValue().get("user");
		System.out.println(user1);
	}

}
