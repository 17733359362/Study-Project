package com.nuist.apo.service;

import com.nuist.apo.entity.User;
import com.nuist.apo.vo.UserVO;

/**
 * @Author wtx
 * @Date 2024/5/30
 */
public interface UserService {
    String validate();

    UserVO register(User user);
}
