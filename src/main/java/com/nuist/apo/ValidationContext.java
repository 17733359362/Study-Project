package com.nuist.apo;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author wtx
 * @Date 2024/5/31
 *
 * 策略工场
 * 使用工厂和上下文选择执行挡板方法的逻辑，而不是在挡板方法中逻辑写死，如果使用自定义挡板逻辑可以使用该方式
 * 但是如果只需要返回固定结果，那么可以采用注解的方式为对应校验方法返回固定结果
 */
@Component
public class ValidationContext {
    // 对应消费者需要执行的挡板方法
    private Map<Class<? extends Strategy>, Strategy> strategies = new HashMap<>();

    public ValidationContext(List<Strategy> strategyList) {
        strategyList.forEach(strategy -> this.strategies.put(strategy.getClass(), strategy));
    }

    /**
     *
     * @param strategyClass 要执行的挡板类
     * @param params 需要被拦截的方法的参数
     * @param defaultValue 默认返回值
     * @return
     */
    public Object execute(Class<?> strategyClass, String defaultValue, Object...params) {
        return strategies.get(strategyClass).validate(defaultValue, params);
    }
}
