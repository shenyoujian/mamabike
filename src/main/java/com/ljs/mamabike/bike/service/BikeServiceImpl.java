package com.ljs.mamabike.bike.service;

import com.ljs.mamabike.bike.dao.BikeMapper;
import com.ljs.mamabike.bike.entity.Bike;
import com.ljs.mamabike.bike.entity.BikeNoGen;
import com.ljs.mamabike.common.exception.MaMaBikeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author ljs
 * @Description TODO
 * @Date 2018/10/4 23:31
 **/
@Service("bikeServiceImpl")
@Slf4j
public class BikeServiceImpl implements BikeService {

    @Autowired
    private BikeMapper bikeMapper;

    /**
     * Author ljs
     * Description 生成单车
     * Date 2018/10/4 23:36
     **/
    @Override
    public void generateBike() throws MaMaBikeException {
        //生成单车编号
        BikeNoGen bikeNo = new BikeNoGen();
        bikeMapper.generateBikeNo(bikeNo);
        Long no = bikeNo.getAutoIncNo();
        //生成单车
        Bike bike = new Bike();
        bike.setNumber(no);
        bike.setType((byte) 2);
        bikeMapper.insertSelective(bike);
    }
}
