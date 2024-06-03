package com.nuist.currentlimit;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author wtx
 * @Date 2024/5/23
 * @Desc 滑动窗口算法
 * 将单位时间周期又划分成n个小周期，分别根据每个小周期内接口的访问次数，并根据时间滑动自动删除过期的小周期（tcp就是根据这个来实现对发送字节的大小控制）
 * 优点：简单易懂，精度高可以调整时间粒度来调节限流效果，可拓展性，可与和其他限流算法结合使用
 * 缺点：无法应对突发请求，突发请求会被大量拒绝，会损失大量请求
 */
public class SlidingWindowRateLimiter {
    /**
     * 最大请求数
     */
    private final Long limiter;

    /**
     * 窗口大小
     */
    private final Long windowSize;

    /**
     * 请求队列，存储请求进来的毫秒数
     */
    private final Queue<Long> requests;

    public SlidingWindowRateLimiter(Long limiter, Long windowSize) {
        this.limiter = limiter;
        this.windowSize = windowSize;
        requests = new ConcurrentLinkedQueue<>();
    }

    public boolean tryAcquire() {
        // 移除窗口时间外的请求
        while (!this.requests.isEmpty() && System.currentTimeMillis() - requests.peek() > windowSize) {
            requests.poll();
        }
        // 判断当前窗口内的请求数量是否超过最大值
        if (requests.size() < limiter) {
            // 不超过则通过该请求
            requests.offer(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        SlidingWindowRateLimiter rateLimiter = new SlidingWindowRateLimiter(10L, 1000L);
        for (int i = 0; i < 80; i++) {
            System.out.println("Request " + (i + 1) + ": " + (rateLimiter.tryAcquire() ? "Accepted" : "Rejected"));
            Thread.sleep(random.nextInt(300));
        }
    }
}
