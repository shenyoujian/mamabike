package com.ljs.mamabike.common.utils;

import com.google.gson.Gson;
import com.ljs.mamabike.common.constants.Constants;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * @Author ljs
 * @Description 七牛上传工具类
 * @Date 2018/10/4 10:31
 **/
public class QiniuFileUploadUtil {

    public static String uploadHeadImg(MultipartFile file) throws IOException {
        Configuration cfg = new Configuration(Zone.zone0());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(Constants.QINIU_ACCESS_KEY, Constants.QINIU_SECRET_KEY);
        String upToken = auth.uploadToken(Constants.QINIU_HEAD_IMG_BUCKET_NAME);
        //key为null表示使用文件的哈希值表示
        //内存中的字节数组上传到空间中
        Response response = uploadManager.put(file.getBytes(), null, upToken);
        //解析上传成功的结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }
}
