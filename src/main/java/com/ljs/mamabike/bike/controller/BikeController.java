package com.ljs.mamabike.bike.controller;

import com.ljs.mamabike.bike.entity.BikeLocation;
import com.ljs.mamabike.bike.entity.Point;
import com.ljs.mamabike.bike.service.BikeGeoService;
import com.ljs.mamabike.bike.service.BikeService;
import com.ljs.mamabike.common.constants.Constants;
import com.ljs.mamabike.common.constants.Parameters;
import com.ljs.mamabike.common.exception.MaMaBikeException;
import com.ljs.mamabike.common.resp.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author ljs
 * @Description TODO
 * @Date 2018/10/4 23:33
 **/
@RestController
@Slf4j
@RequestMapping("bike")
public class BikeController {

    @Autowired
    private BikeGeoService bikeGeoService;

    @Autowired
    private Parameters parameters;


    @RequestMapping(value = "/findAroundBike", method = RequestMethod.POST)
    public ApiResult<String> findAroundBike(@RequestBody Point point){
        ApiResult result = new ApiResult();
        try {
            List<BikeLocation> bikeList = bikeGeoService.findNearBikeAndDis(parameters.getCollection(), null, point, parameters.getLimit(), parameters.getMaxDistance());
            result.setData(bikeList);
            result.setMessage("查询单车成功");
        } catch (MaMaBikeException e) {
            result.setCode(e.getStatusCode());
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Fail to find around bike info", e);
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            result.setMessage("内部错误");
        }
        return result;
    }


}
