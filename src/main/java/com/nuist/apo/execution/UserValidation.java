package com.nuist.apo.execution;

import com.nuist.apo.CustomExecution;
import com.nuist.apo.Strategy;

/**
 * @Author wtx
 * @Date 2024/5/31
 */
@CustomExecution
public class UserValidation implements Strategy {
    @Override
    public Object validate(String defaultValue, Object... params) {
        return "222";
    }
}
