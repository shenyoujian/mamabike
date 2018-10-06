package com.ljs.mamabike.bike.service;

import com.ljs.mamabike.bike.entity.BikeLocation;
import com.ljs.mamabike.bike.entity.Point;
import com.ljs.mamabike.common.exception.MaMaBikeException;
import com.mongodb.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ljs
 * @Description 单车定位服务类，使用spring-data来操作mongodb
 * @Date 2018/10/5 13:14
 **/
@Component("bikeGeoService")
@Slf4j
public class BikeGeoService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Author ljs
     * Description 查找某经坐标点附近某范围内坐标点 由近到远
     * Date 2018/10/5 17:37
     **/
    public List<BikeLocation> findNearBike(String collection, String locationField, Point center,
                                           long minDistance, long maxDistance, DBObject query, DBObject fields, int limit) throws MaMaBikeException {
        try {
            if (query == null) {
                query = new BasicDBObject();
            }
            query.put(locationField,
                    new BasicDBObject("$nearSphere",
                            new BasicDBObject("$geometry",
                                    new BasicDBObject("type", "Point")
                                            .append("coordinates", new double[]{center.getLongitude(), center.getLatitude()}))
                                    .append("$minDistance", minDistance)
                                    .append("$maxDistance", maxDistance)
                    ));
            query.put("status", 1);
            List<DBObject> objList = mongoTemplate.getCollection(collection).find(query, fields).limit(limit).toArray();
            List<BikeLocation> result = new ArrayList<>();
            for (DBObject obj : objList) {
                BikeLocation location = new BikeLocation();
                location.setBikeNumber(((Integer) obj.get("bike_no")).longValue());
                location.setStatus((Integer) obj.get("status"));
                BasicDBList coordinates = (BasicDBList) ((BasicDBObject) obj.get("location")).get("coordinates");
                Double[] temp = new Double[2];
                coordinates.toArray(temp);
                location.setCoordinates(temp);
                result.add(location);
            }
            return result;
        } catch (Exception e) {
            log.error("fail to find around bike", e);
            throw new MaMaBikeException("查找附近单车失败");
        }
    }

    /**
     * Author ljs
     * Description 查找某经坐标点附近某范围内坐标点 由近到远 并且计算距离
     * Date 2018/10/6 14:34
     **/
    public List<BikeLocation> findNearBikeAndDis(String collection, DBObject query, Point point, int limit, long maxDistance) throws MaMaBikeException {

        try {
            if (query == null) {
                query = new BasicDBObject();
            }
            List<DBObject> pipeLine = new ArrayList<>();
            BasicDBObject aggregate = new BasicDBObject("$geoNear",
                    new BasicDBObject("near", new BasicDBObject("type", "Point").append("coordinates", new double[]{point.getLongitude(), point.getLatitude()}))
                            .append("distanceField", "distance")
                            .append("num", limit)
                            .append("maxDistance", maxDistance)
                            .append("spherical", true)
                            .append("query", new BasicDBObject("status", 1))
            );
            pipeLine.add(aggregate);
            Cursor cursor = mongoTemplate.getCollection(collection).aggregate(pipeLine, AggregationOptions.builder().build());
            List<BikeLocation> result = new ArrayList<>();
            while (cursor.hasNext()) {
                DBObject obj = cursor.next();
                BikeLocation location = new BikeLocation();
                location.setBikeNumber(Long.valueOf((String) obj.get("bike_no")));
                BasicDBList coordinates = (BasicDBList) ((BasicDBObject) obj.get("location")).get("coordinates");
                Double[] temp = new Double[2];
                coordinates.toArray(temp);
                location.setCoordinates(temp);
                location.setDistance((Double) obj.get("distance"));
                result.add(location);
            }

            return result;
        } catch (Exception e) {
            log.error("fail to find around bike", e);
            throw new MaMaBikeException("查找附近单车失败");
        }
    }

}
