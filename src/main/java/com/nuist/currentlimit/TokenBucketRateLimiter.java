package com.nuist.currentlimit;

import com.nuist.util.ExecuteUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author wtx
 * @Date 2024/5/23
 * @Desc 令牌桶限流算法
 */
public class TokenBucketRateLimiter {
    /**
     * 桶容量
     */
    private int capacity;

    /**
     * 当前剩余可用令牌数量
     */
    private AtomicInteger tokens;

    /**
     * 令牌发放速率
     */
    private int rate;

    /**
     * 上一次令牌发放时间
     */
    private Long lastTime;


    public TokenBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.tokens = new AtomicInteger(0);
        this.rate = rate;
        this.lastTime = System.currentTimeMillis();
    }


    /**
     * 检查是否可以消费指定令牌数
     *
     * @param applyCount 请求数量
     * @return
     */
    public synchronized boolean allowRequests(int applyCount) {
        long now = System.currentTimeMillis();
        long gap = now - this.lastTime;

        // 计算时间段内的可用的令牌数
        int reservePermits = (int) (gap * rate / 1000);
        int allPermits = reservePermits + this.tokens.get();
        // 更新当前可用令牌数
        tokens.set(Math.min(this.capacity, allPermits));
        // 剩余令牌不够，拒绝访问
        if (tokens.get() < applyCount) {
            return false;
        } else {
            // 更新剩余令牌数
            tokens.addAndGet(-applyCount);
            // 更新时间
            lastTime = now;
            return true;
        }
    }

    public static void main(String[] args) {
        // 被限制的次数
        AtomicInteger limited = new AtomicInteger(0);
        // 线程数
        final int threads = 2;
        // 每条线程的执行轮数
        final int turns = 20;

        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(2, 2);

        // 同步器
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            ExecuteUtil.poll.submit(() -> {
                try {
                    for (int j = 0; j < turns; j++) {
                        boolean intercepted = rateLimiter.allowRequests(1);
                        if (intercepted) {
                            // 被限制的次数累积
                            limited.getAndIncrement();
                        }

                        Thread.sleep(200);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                //等待所有线程结束
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        float time = (System.currentTimeMillis() - start) / 1000F;
        //输出统计结果

        System.out.println("限制的次数为：" + limited.get() +
                ",通过的次数为：" + (threads * turns - limited.get()));
        System.out.println("限制的比例为：" + (float) limited.get() / (float) (threads * turns));
        System.out.println("运行的时长为：" + time);
    }
}
