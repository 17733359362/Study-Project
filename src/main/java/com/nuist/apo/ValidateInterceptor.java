package com.nuist.apo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * @Author wtx
 * @Date 2024/5/30
 * <p>
 * 分配对应的挡板实例执行挡板逻辑
 */
@Aspect
@Component
public class ValidateInterceptor {

    @Autowired
    private ValidationContext validationContext;

    @Around(value = "@annotation(com.nuist.apo.BlockExecution)")
    Object interceptor(JoinPoint joinPoint) throws JsonProcessingException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        BlockExecution annotation = method.getAnnotation(BlockExecution.class);
        String defaultValue = annotation.defaultValue();
        Class<?> returnType = annotation.type();
        ObjectMapper mapper = new ObjectMapper();
        // 执行自定义校验挡板逻辑
        if (annotation.customLogic()) {
            Object[] args = joinPoint.getArgs();
            return validationContext.execute(returnType, defaultValue, args);
        }
        return mapper.readValue(defaultValue, returnType); // 使用注解中的固定值
    }
}
