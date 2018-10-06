package com.ljs.mamabike.bike.entity;

import lombok.Data;

/**
 * Author ljs
 * Description TODO
 * Date 2018/10/5 13:32
 **/
@Data
public class BikeLocation {

    private String id;

    private Long bikeNumber;

    private int status;

    private Double[] coordinates;

    private Double distance;

}
