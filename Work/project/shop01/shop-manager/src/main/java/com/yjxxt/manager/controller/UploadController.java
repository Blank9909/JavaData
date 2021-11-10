package com.yjxxt.manager.controller;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.yjxxt.common.result.FileResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("fileUpload")
public class UploadController {

    @Value("${qiniu.ak}")
    private String ak;
    @Value("${qiniu.sk}")
    private String sk;
    @Value("${qiniu.bucket}")
    private String bucket;
    @Value("${qiniu.domain}")
    private String domain;


    @RequestMapping("save")
    @ResponseBody
    public FileResult uploadFile(@RequestParam(name = "file") MultipartFile multipartFile) {
        FileResult fileResult = new FileResult();
        if (null == multipartFile || multipartFile.isEmpty()) {
            fileResult.setError("文件内容不能为空!");
            return fileResult;
        }
        try {
            InputStream is = multipartFile.getInputStream();
            //构造一个带指定 Region 对象的配置类
            Configuration cfg = new Configuration(Region.region0());
            //...其他参数参考类注释
            UploadManager uploadManager = new UploadManager(cfg);
            String fileName = new SimpleDateFormat("yyyy/MM/dd/").format(new Date())
                    + System.currentTimeMillis() + multipartFile.getOriginalFilename()
                    .substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            Auth auth = Auth.create(ak, sk);
            String upToken = auth.uploadToken(bucket);
            Response response = uploadManager.put(is, fileName, upToken,null,null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            fileResult.setSuccess("文件上传成功!");
            fileResult.setFileUrl(domain+putRet.key);
            return fileResult;
        } catch (Exception ex) {
            ex.printStackTrace();
            fileResult.setError("文件上传失败!");
            return fileResult;
        }
    }
}
