package com.ljs.mamabike.bike.service;

import com.ljs.mamabike.common.exception.MaMaBikeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author ljs
 * @Description TODO
 * @Date 2018/10/4 23:32
 **/
public interface BikeService {

    public void generateBike() throws MaMaBikeException;

}
