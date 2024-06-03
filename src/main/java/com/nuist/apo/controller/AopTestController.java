package com.nuist.apo.controller;

import com.nuist.apo.entity.User;
import com.nuist.apo.service.UserService;
import com.nuist.apo.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author wtx
 * @Date 2024/5/30
 */
@RestController
public class AopTestController {

    @Autowired
    private UserService userService;

    @GetMapping("/interceptor")
    public String interceptor() {
        return userService.validate();
    }

    @GetMapping("/getUser")
    public UserVO getUser() {
        return userService.register(null);
    }
}
