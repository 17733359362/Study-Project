package com.nuist.apo;

/**
 * @Author wtx
 * @Date 2024/5/31
 * 顶层接口
 * 定义挡板方法
 */
public interface Strategy {
    Object validate(String defaultValue, Object...params);
}
