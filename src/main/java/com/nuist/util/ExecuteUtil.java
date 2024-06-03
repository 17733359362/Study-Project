package com.nuist.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author wtx
 * @Date 2024/5/23
 */
public class ExecuteUtil {
    public static final ExecutorService poll = Executors.newFixedThreadPool(10);
}
