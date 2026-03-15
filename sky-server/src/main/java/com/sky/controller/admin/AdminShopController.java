package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/shop")
public class AdminShopController {
    public static final String KEY = "SHOP_STATUS";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 设置营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置营业状态：{}",status == 1 ? "营业中" : "打烊中");
        stringRedisTemplate.opsForValue().set(KEY, String.valueOf(status));
        return Result.success();

    }

    /**
     * 查询营业状态
     * @return
     */
    @GetMapping("/status")
    public Result getStatus(){
        try {
            Integer status = Integer.valueOf(stringRedisTemplate.opsForValue().get(KEY));
            log.info("查询营业状态为：{}", status == 1 ? "营业中" : "打烊中");
            return Result.success(status);
        } catch (Exception e) {
            return Result.success(StatusConstant.DISABLE);
        }


    }

}
