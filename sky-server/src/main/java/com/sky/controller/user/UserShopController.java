package com.sky.controller.user;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/shop")
public class UserShopController {
    public static final String KEY = "SHOP_STATUS";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取营业状态
     * @return
     */
    @GetMapping("/status")
    public Result getStatus(){
        Integer status = Integer.valueOf(stringRedisTemplate.opsForValue().get(KEY));
        log.info("查询营业状态为：{}",status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }

}
