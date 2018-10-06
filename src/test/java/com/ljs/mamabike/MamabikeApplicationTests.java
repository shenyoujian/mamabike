package com.ljs.mamabike;

import com.ljs.mamabike.bike.controller.BikeController;
import com.ljs.mamabike.bike.entity.Point;
import com.ljs.mamabike.bike.service.BikeGeoService;
import com.ljs.mamabike.bike.service.BikeService;
import com.ljs.mamabike.common.exception.MaMaBikeException;
import com.ljs.mamabike.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    private TestRestTemplate restTemplate;    //模拟http请求，访问rest资源

    @LocalServerPort
    private int port;    //使用随机端口

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Autowired
    @Qualifier("bikeServiceImpl")
    private BikeService bikeService;

    @Autowired
    @Qualifier("bikeGeoService")
    private BikeGeoService bikeGeoService;

    @Test
    public void contextLoads() {
        String result = restTemplate.getForObject("/user/hello", String.class);
        System.out.println(result);
    }

    @Test
    public void generateBike() throws MaMaBikeException {
        bikeService.generateBike();
    }

    @Test
    public void findNearBike() throws MaMaBikeException {
        bikeGeoService.findNearBike("bike-poistion", "location"
                , new Point( 113.500298, 23.457256), 0, 50, null, null, 10);
    }

    @Test
    public void findNearBikeAndDis() throws MaMaBikeException {
        bikeGeoService.findNearBikeAndDis("bike-poistion", null
                , new Point( 113.500298, 23.457256), 10, 50);
    }

}
