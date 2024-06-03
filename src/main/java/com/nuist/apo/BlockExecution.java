package com.nuist.apo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author wtx
 * @Date 2024/5/30
 *
 * 校验挡板，被该注解标注的方法将不会被执行
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@Target(ElementType.METHOD) // 作用与方法上
public @interface BlockExecution {
    /**
     * 挡板实例默认返回值
     */
    String defaultValue() default "";

    /**
     * 使用具体挡板实例执行校验方法  挡板实例class
     */
    Class<?> type() default Void.class;

    /**
     * 是否使用自定义挡板逻辑
     */
    boolean customLogic() default false;
}
