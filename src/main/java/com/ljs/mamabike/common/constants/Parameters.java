package com.ljs.mamabike.common.constants;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author ljs
 * @Description 读取配置文件参数
 * @Date 2018/9/4 14:37
 **/

@Data
@Component
public class Parameters {

    /*****redis config start*******/
    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private int redisPort;
    //@Value("${redis.auth}")
    //private String redisAuth;
    @Value("${redis.max-idle}")
    private int redisMaxTotal;
    @Value("${redis.max-total}")
    private int redisMaxIdle;
    @Value("${redis.max-wait-millis}")
    private int redisMaxWaitMillis;
    /*****redis config end*******/

    @Value("#{'${security.noneSecurityPath}'.split(',')}")
    private List noneSecurityPath;
}
