package com.nuist.apo.service.impl;

import com.nuist.apo.BlockExecution;
import com.nuist.apo.entity.User;
import com.nuist.apo.execution.UserValidation;
import com.nuist.apo.service.UserService;
import com.nuist.apo.vo.UserVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author wtx
 * @Date 2024/5/30
 */
@Service
public class UserServiceImpl implements UserService {

    @BlockExecution(type = UserValidation.class, customLogic = true) // 使用自定义逻辑
//    @BlockExecution(type = Boolean.class, defaultValue = "333")
    @Override
    public String validate() {
        System.out.println("我是校验方法....");
        return "111";
    }

    @BlockExecution(type = UserVO.class, defaultValue = "{\"id\":2,\"name\":\"王五\"}")
    @Override
    public UserVO register(User user) {
        UserVO userVO = new UserVO();
        userVO.setId(2L);
        userVO.setName("李四");
        return userVO;
    }


}
