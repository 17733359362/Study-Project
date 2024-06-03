package com.nuist.currentlimit;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author wtx
 * @Date 2024/5/23
 * @Desc 固定窗口限流算法
 * 固定窗口算法（计数器算法）：是一种比较简单的限流算法，它把时间划分为固定的时间窗口，每个窗口内允许的请求次数设置限制。如果在一个时间窗口内，请求次数超过了上限，那么就会触发限流
 * 这里只是简单演示，分布式服务需要加锁
 * 将时间分为固定窗口，将请求按时间顺序放入时间窗口，如果超过设置时间窗口的阈值就返回限流结果，但是在两个时间窗口替换间隙可能会有2n请求并发。
 * 优点：实现简单，便于理解
 * 缺点：存在明显的临界问题，切换间隙可产生产生2n请求：0.75秒-1.0秒产生n个请求，刷新，1.0-1.5s又产生n个请求，这n个请求各自窗口满足要求，但是再它门组成的窗口中并发量达到了2n
 */
public class FixedWindowRateLimiter {

    /**
     * 当前窗口请求限制数量
     */
    private final AtomicInteger count = new AtomicInteger();

    /**
     * 当前窗口最大请求数
     */
    private static final Long limit = 2L;

    /**
     * 窗口大小 1000毫秒
     */
    private static final Long windowSize = 1000L;
    /**
     * 窗口边界
     */
    private Long windowBoundary = 0L;

    /**
     * @return 请求是否通过
     */
    public boolean fixedWindow() {
        long currentTime = System.currentTimeMillis();
        // 超过窗口最大宽度  重置窗口
        if (currentTime - windowBoundary > windowSize) {
            count.set(0);
            windowBoundary = currentTime;
        }
        // 超过窗口最大请求数  截断
        if (count.get() >= limit) {
            return false;
        }
        // 请求数自增
        count.getAndIncrement();
        return true;
    }

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        FixedWindowRateLimiter rateLimiter = new FixedWindowRateLimiter();
        for (int i = 0; i < 80; i++) {
            System.out.println(rateLimiter.fixedWindow());
            // 模拟请求延迟
            Thread.sleep(random.nextInt(100));
        }
    }
}
