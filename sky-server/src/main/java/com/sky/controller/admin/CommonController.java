package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliyunOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    AliyunOSSUtil aliyunOSSUtil;
    @RequestMapping("/upload")
    public Result<String> upload(@RequestBody MultipartFile file){
        log.info("文件上传开始:{}",file);
        try {
           String url = aliyunOSSUtil.upload(file.getBytes(), Objects.requireNonNull(file.getOriginalFilename()));
           return Result.success(url);
        } catch (Exception e) {
            log.error("文件上传失败:{}",e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
