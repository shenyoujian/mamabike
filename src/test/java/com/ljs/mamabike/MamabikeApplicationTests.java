package com.ljs.mamabike;

import com.ljs.mamabike.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(classes = MamabikeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MamabikeApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;	//模拟http请求，访问rest资源

	@LocalServerPort
	private int port;	//使用随机端口

	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;

	@Test
	public void contextLoads() {
		String result = restTemplate.getForObject("/user/hello",String.class);
		System.out.println(result);
	}

	@Test
	public void test(){
		//Logger logger = LoggerFactory.getLogger(MamabikeApplicationTests.class);
		try{
			userService.login();
		}catch (Exception e){
			log.error("出错了", e);
		}
	}

}
