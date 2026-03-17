package com.sky.controller.user;

import com.sky.dto.UserLoginDTO;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * @description: 用户登录
     * @param:  * @param userLoginDTO 参考{@link UserLoginDTO}
     * @return: com.sky.result.Result<com.sky.vo.UserLoginVO>
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        return Result.success(userService.wxlogin(userLoginDTO));
    }

}
